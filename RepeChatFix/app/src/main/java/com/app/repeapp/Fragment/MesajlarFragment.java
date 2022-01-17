package com.app.repeapp.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.app.repeapp.Adapter.MesajlarAdapter;
import com.app.repeapp.Model.MesajIstegi;
import com.app.repeapp.R;

import java.util.ArrayList;

public class MesajlarFragment extends Fragment {
    private View v;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFireStore;
    private Query mQuery;
    private ArrayList<MesajIstegi> mArrayList;
    private ArrayList<String> mSonMesajList;
    private MesajIstegi mesajIstegi;
    private MesajlarAdapter mesajlarAdapter;
    private FirebaseUser mUser;

    private Query sonMsgQuery;
    private int sonMsgIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_mesajlar, container, false);

        mFireStore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mArrayList = new ArrayList<>();
        mSonMesajList = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.mesajlar_fragment_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        mQuery = mFireStore.collection("Kullanıcılar").document(mUser.getUid()).collection("Kanal");
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null){
                    mArrayList.clear();
                    sonMsgIndex = 0;

                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        mesajIstegi = snapshot.toObject(MesajIstegi.class);

                        if (mesajIstegi != null) {
                            mArrayList.add(mesajIstegi);

                            sonMsgQuery = mFireStore.collection("ChatKanalları").document(mesajIstegi.getKanalId()).collection("Mesajlar")
                                    .orderBy("mesajTarihi", Query.Direction.DESCENDING)
                                    .limit(1);
                            sonMsgQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value2, @Nullable FirebaseFirestoreException error) {
                                    if (error == null && value2 != null){
                                        mSonMesajList.clear();

                                        for (DocumentSnapshot snp : value2.getDocuments()) {
                                            mSonMesajList.add(snp.getData().get("mesajIcerigi").toString());
                                            sonMsgIndex++;

                                            if (sonMsgIndex == value.getDocuments().size()){
                                                mesajlarAdapter = new MesajlarAdapter(mArrayList, v.getContext(), mSonMesajList);
                                                mRecyclerView.setAdapter(mesajlarAdapter);
                                                sonMsgIndex = 0;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        return v;
    }
}