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

import com.app.repeapp.Adapter.PostAdapter;
import com.app.repeapp.Model.Post;
import com.app.repeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FeedFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;


    RecyclerView recyclerViewFeed;
    public FeedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerViewFeed=view.findViewById(R.id.recyclerViewFeed);


        postArrayList=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();



        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter=new PostAdapter(postArrayList);
        recyclerViewFeed.setAdapter(postAdapter);










        getData();

        return view;

    }
    private void getData(){
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Toast.makeText(getContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if (value !=null){
                    for (DocumentSnapshot snapshot:value.getDocuments()){
                        Map<String,Object> data=snapshot.getData();
                        String userEmail=(String) data.get("useremail");
                        String comment=(String) data.get("comment");
                        String downloadUrl=(String) data.get("downloadurl");





                        Post post=new  Post("   "+userEmail,comment,downloadUrl);
                        postArrayList.add(post);

                    }

                    postAdapter.notifyDataSetChanged();
                }
            }
        });



    }
}