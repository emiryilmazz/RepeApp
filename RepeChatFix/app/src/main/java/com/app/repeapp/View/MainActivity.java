package com.app.repeapp.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.repeapp.Fragment.FeedFragment;
import com.app.repeapp.Fragment.PostFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.app.repeapp.Adapter.MesajIstekleriAdapter;
import com.app.repeapp.Fragment.KullanicilarFragment;
import com.app.repeapp.Fragment.MesajlarFragment;
import com.app.repeapp.Fragment.ProfilFragment;
import com.app.repeapp.Model.Kullanici;
import com.app.repeapp.Model.MesajIstegi;
import com.app.repeapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private HashMap<String, Object> mData;

    private BottomNavigationView mBottomView;
    private KullanicilarFragment kullanicilarFragment;
    private MesajlarFragment mesajlarFragment;
    private ProfilFragment profilFragment;
    private PostFragment postFragment;
    private FeedFragment feedFragment;

    private FragmentTransaction transaction;
    private Toolbar mToolbar;
    private RelativeLayout mRelaNotif;
    private ImageView sharePost;
    private TextView txtBildirimSayisi;

    private FirebaseFirestore mFireStore;
    private Query mQuery;
    private FirebaseUser mUser;
    private MesajIstegi mMesajIstegi;
    private ArrayList<MesajIstegi> mesajIstegiList;

    private Dialog mesajIstekleriDialog;
    private ImageView mesajIstekleriKapat;
    private RecyclerView mesajIstekleriRecyclerView;
    private MesajIstekleriAdapter mAdapter;

    private DocumentReference mRef;
    private Kullanici mKullainici;

    private void init(){
        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = mFireStore.collection("Kullanıcılar").document(mUser.getUid());
        mRef.get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                            mKullainici = documentSnapshot.toObject(Kullanici.class);
                    }
                });

        mesajIstegiList = new ArrayList<>();

        mBottomView = (BottomNavigationView)findViewById(R.id.main_activity_bottomView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mRelaNotif = (RelativeLayout)findViewById(R.id.bar_layout_relaNotif);
        txtBildirimSayisi = (TextView)findViewById(R.id.bar_layout_txtBildirimSayisi);
        sharePost=findViewById(R.id.bar_layout_sharePost);

        kullanicilarFragment = new KullanicilarFragment();
        mesajlarFragment = new MesajlarFragment();
        profilFragment = new ProfilFragment();
        postFragment=new PostFragment();
        feedFragment=new FeedFragment();


        fragmentiAyarla(feedFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        mQuery = mFireStore.collection("Mesajİstekleri").document(mUser.getUid()).collection("İstekler");
        mQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null){
                    txtBildirimSayisi.setText(String.valueOf(value.getDocuments().size()));
                    txtBildirimSayisi.setTextColor(Color.GREEN);
                    mesajIstegiList.clear();
                    if (txtBildirimSayisi.getText()=="0")
                    {
                        txtBildirimSayisi.setText(String.valueOf(value.getDocuments().size()));
                        txtBildirimSayisi.setTextColor(Color.RED);
                        mesajIstegiList.clear();
                    }

                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        mMesajIstegi = snapshot.toObject(MesajIstegi.class);
                        mesajIstegiList.add(mMesajIstegi);
                    }
                }
            }
        });

        mRelaNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesajIstekleriDialog();
            }
        });

        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentiAyarla(postFragment);
            }
        });





        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){




                    case R.id.bottom_nav_ic_people:
                        sharePost.setVisibility(View.INVISIBLE);

                        mRelaNotif.setVisibility(View.INVISIBLE);

                        fragmentiAyarla(kullanicilarFragment);
                        return true;

                    case R.id.bottom_nav_ic_message:
                        sharePost.setVisibility(View.INVISIBLE);
                        mRelaNotif.setVisibility(View.VISIBLE);
                        fragmentiAyarla(mesajlarFragment);
                        return true;

                    case R.id.bottom_nav_ic_profile:
                        sharePost.setVisibility(View.INVISIBLE);
                        mRelaNotif.setVisibility(View.INVISIBLE);
                        fragmentiAyarla(profilFragment);
                        return true;

                    default:
                        Intent intent=new Intent(mBottomView.getContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mRelaNotif.setVisibility(View.VISIBLE);
                        sharePost.setVisibility(View.VISIBLE);
                        fragmentiAyarla(feedFragment);

                        return true;
                }
            }
        });
    }

    private void fragmentiAyarla(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_activity_frameLayout, fragment);
        transaction.commit();
    }

    private void mesajIstekleriDialog(){
        mesajIstekleriDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        mesajIstekleriDialog.setContentView(R.layout.custom_dialog_gelen_mesaj_istekleri);

        mesajIstekleriKapat = mesajIstekleriDialog.findViewById(R.id.custom_dialog_gelen_mesaj_istekleri_imgKapat);
        mesajIstekleriRecyclerView = mesajIstekleriDialog.findViewById(R.id.custom_dialog_gelen_mesaj_istekleri_recyclerView);

        mesajIstekleriKapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesajIstekleriDialog.dismiss();
            }
        });

        mesajIstekleriRecyclerView.setHasFixedSize(true);
        mesajIstekleriRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (mesajIstegiList.size() > 0){
            mAdapter = new MesajIstekleriAdapter(mesajIstegiList, this, mKullainici.getKullaniciId(), mKullainici.getKullaniciIsmi(), mKullainici.getKullaniciProfil());
            mesajIstekleriRecyclerView.setAdapter(mAdapter);
        }

        mesajIstekleriDialog.show();
    }

    private void kullaniciSetOnline(Boolean b){
        mData = new HashMap<>();
        mData.put("kullaniciOnline", b);

        mFireStore.collection("Kullanıcılar").document(mUser.getUid())
                .update(mData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        kullaniciSetOnline(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        kullaniciSetOnline(false);
    }
}