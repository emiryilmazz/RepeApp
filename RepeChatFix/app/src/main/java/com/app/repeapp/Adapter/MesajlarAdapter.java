package com.app.repeapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.repeapp.Model.MesajIstegi;
import com.app.repeapp.R;
import com.app.repeapp.View.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajlarAdapter extends RecyclerView.Adapter<MesajlarAdapter.MesajlarHolder> {
    private ArrayList<MesajIstegi> mArrayList;
    private ArrayList<String> mSonMsgList;
    private Context mContext;
    private MesajIstegi mesajIstegi;
    private View v;
    private int kPos;
    private Intent chatIntent;

    public MesajlarAdapter(ArrayList<MesajIstegi> mArrayList, Context mContext, ArrayList<String> mSonMsgList) {
        this.mArrayList = mArrayList;
        this.mContext = mContext;
        this.mSonMsgList = mSonMsgList;
    }

    @NonNull
    @Override
    public MesajlarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(mContext).inflate(R.layout.mesajlar_item, parent, false);
        return new MesajlarHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MesajlarHolder holder, int position) {
        mesajIstegi = mArrayList.get(position);
        holder.kullaniciIsmi.setText(mesajIstegi.getKullaniciIsim());
        holder.sonMesaj.setText(mSonMsgList.get(position));

        if (mesajIstegi.getKullaniciProfil().equals("default"))
            holder.kullaniciProfil.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(mesajIstegi.getKullaniciProfil()).resize(66, 66).into(holder.kullaniciProfil);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kPos = holder.getAdapterPosition();

                if (kPos != RecyclerView.NO_POSITION){
                    chatIntent = new Intent(mContext, ChatActivity.class);
                    chatIntent.putExtra("kanalId", mArrayList.get(kPos).getKanalId());
                    chatIntent.putExtra("hedefId", mArrayList.get(kPos).getKullaniciId());
                    chatIntent.putExtra("hedefProfil", mArrayList.get(kPos).getKullaniciProfil());
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(chatIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MesajlarHolder extends RecyclerView.ViewHolder{
        TextView kullaniciIsmi, sonMesaj;
        CircleImageView kullaniciProfil;

        public MesajlarHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciIsmi = itemView.findViewById(R.id.mesajlar_item_txtKullaniciIsim);
            kullaniciProfil = itemView.findViewById(R.id.mesajlar_item_imgKullaniciProfil);
            sonMesaj = itemView.findViewById(R.id.mesajlar_item_txtSonMesaj);
        }
    }
}
