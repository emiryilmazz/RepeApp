package com.app.repeapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.app.repeapp.Model.Kullanici;
import com.app.repeapp.Model.MesajIstegi;
import com.app.repeapp.R;
import com.app.repeapp.View.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class KullaniciAdapter extends RecyclerView.Adapter<KullaniciAdapter.KullaniciHolder> {
    private ArrayList<Kullanici> mKullaniciList;
    private Context mContext;
    private View v;
    private Kullanici mKullanici;
    private int kPos;

    private Dialog mesajDialog;
    private ImageView imgIptal;
    private LinearLayout linearGonder;
    private CircleImageView imgProfil;
    private EditText editMesaj;
    private TextView txtIsim;
    private String txtMesaj;
    private Window mesajWindow;

    private FirebaseFirestore mFireStore;
    private DocumentReference mRef;
    private String mUID, mIsim, mProfilUrl, kanalId, mesajDocId;
    private MesajIstegi mesajIstegi;
    private HashMap<String, Object> mData;

    private Intent chatIntent;

    public KullaniciAdapter(ArrayList<Kullanici> mKullaniciList, Context mContext, String mUID, String mIsim, String  mProfilUrl) {
        this.mKullaniciList = mKullaniciList;
        this.mContext = mContext;
        mFireStore = FirebaseFirestore.getInstance();
        this.mUID = mUID;
        this.mIsim = mIsim;
        this.mProfilUrl = mProfilUrl;
    }

    @NonNull
    @Override
    public KullaniciHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(mContext).inflate(R.layout.kullanici_item, parent, false);
        return new KullaniciHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final KullaniciHolder holder, int position) {
        mKullanici = mKullaniciList.get(position);
        holder.kullaniciIsmi.setText(mKullanici.getKullaniciIsmi());

        if (mKullanici.getKullaniciProfil().equals("default"))
            holder.kullaniciProfil.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(mKullanici.getKullaniciProfil()).resize(66, 66).into(holder.kullaniciProfil);

        if (mKullanici.getKullaniciOnline())
            holder.imgOnline.setImageResource(R.drawable.kullanici_cevrimici_bg);
        else
            holder.imgOnline.setImageResource(R.drawable.kullanici_cevrimdisi_bg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kPos = holder.getAdapterPosition();

                if (kPos != RecyclerView.NO_POSITION){
                    mRef = mFireStore.collection("Kullanıcılar").document(mUID).collection("Kanal").document(mKullaniciList.get(kPos).getKullaniciId());
                    mRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        chatIntent = new Intent(mContext, ChatActivity.class);
                                        chatIntent.putExtra("kanalId", documentSnapshot.getData().get("kanalId").toString());
                                        chatIntent.putExtra("hedefId", mKullaniciList.get(kPos).getKullaniciId());
                                        chatIntent.putExtra("hedefProfil", mKullaniciList.get(kPos).getKullaniciProfil());
                                        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(chatIntent);
                                    }else
                                        mesajGonderDialog(mKullaniciList.get(kPos));
                                }
                            });
                }
            }
        });
    }

    private void mesajGonderDialog(final Kullanici kullanici) {
        mesajDialog = new Dialog(mContext);
        mesajDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mesajWindow = mesajDialog.getWindow();
        mesajWindow.setGravity(Gravity.CENTER);
        mesajDialog.setContentView(R.layout.custom_dialog_mesaj_gonder);

        imgIptal = mesajDialog.findViewById(R.id.custom_dialog_mesaj_gonder_imgIptal);
        linearGonder = mesajDialog.findViewById(R.id.custom_dialog_mesaj_gonder_linearGonder);
        imgProfil = mesajDialog.findViewById(R.id.custom_dialog_mesaj_gonder_imgKullaniciProfil);
        editMesaj = mesajDialog.findViewById(R.id.custom_dialog_mesaj_gonder_editMesaj);
        txtIsim = mesajDialog.findViewById(R.id.custom_dialog_mesaj_gonder_txtKullaniciIsim);

        txtIsim.setText(kullanici.getKullaniciIsmi());

        if (kullanici.getKullaniciProfil().equals("default"))
            imgProfil.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(kullanici.getKullaniciProfil()).resize(126, 126).into(imgProfil);

        imgIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesajDialog.dismiss();
            }
        });

        linearGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtMesaj = editMesaj.getText().toString();

                if (!TextUtils.isEmpty(txtMesaj)){
                    kanalId = UUID.randomUUID().toString();

                    mesajIstegi = new MesajIstegi(kanalId, mUID, mIsim, mProfilUrl);
                    mFireStore.collection("Mesajİstekleri").document(kullanici.getKullaniciId()).collection("İstekler").document(mUID)
                            .set(mesajIstegi)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        //Chat Bölümü
                                        mesajDocId = UUID.randomUUID().toString();
                                        mData = new HashMap<>();
                                        mData.put("mesajIcerigi", txtMesaj);
                                        mData.put("gonderen", mUID);
                                        mData.put("alici", kullanici.getKullaniciId());
                                        mData.put("mesajTipi", "text");
                                        mData.put("mesajTarihi", FieldValue.serverTimestamp());
                                        mData.put("docId", mesajDocId);

                                        mFireStore.collection("ChatKanalları").document(kanalId).collection("Mesajlar").document(mesajDocId)
                                                .set(mData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(mContext, "Mesaj İsteğiniz Başarıyla İletildi.", Toast.LENGTH_SHORT).show();
                                                            if (mesajDialog.isShowing())
                                                                mesajDialog.dismiss();
                                                        }else
                                                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else
                                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else
                    Toast.makeText(mContext, "Boş Mesaj Gönderemezsiniz.", Toast.LENGTH_SHORT).show();
            }
        });

        mesajWindow.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        mesajDialog.show();
    }

    @Override
    public int getItemCount() {
        return mKullaniciList.size();
    }

    class KullaniciHolder extends RecyclerView.ViewHolder{
        TextView kullaniciIsmi;
        CircleImageView kullaniciProfil;
        ImageView imgOnline;

        public KullaniciHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciIsmi = itemView.findViewById(R.id.kullanici_item_txtKullaniciIsim);
            kullaniciProfil = itemView.findViewById(R.id.kullanici_item_imgKullaniciProfil);
            imgOnline = itemView.findViewById(R.id.kullanici_item_imgOnline);
        }
    }
}
