package com.app.repeapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.repeapp.R;
import com.app.repeapp.View.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class PostFragment extends Fragment {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ProgressDialog postProgress;
    ImageView imgPost;
    Uri imageData;
    ImageView imgYeni;
    TextView editName,editComment;

    Button sharePost;
    private Intent galeriIntent;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.fragment_post, container, false);

        imgPost=view.findViewById(R.id.post_fragment_imgPost);
        sharePost=view.findViewById(R.id.post_fragment_shareComment);
        imgYeni=view.findViewById(R.id.post_fragment_imgYeniResim);
        editComment=view.findViewById(R.id.post_fragment_editComment);


        auth= FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference= firebaseStorage.getReference();





        imgYeni.setOnClickListener(view1 -> mGetContext.launch("image/*"));
        imgPost.setOnClickListener(view1 -> mGetContext.launch("image/*"));

        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageData!=null){

                    UUID uuid=UUID.randomUUID();
                    String imageName="images/"+uuid+".jpg";


                    postProgress = new ProgressDialog(getContext());
                    postProgress.setTitle("Paylaşılıyor...");
                    postProgress.show();
                    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            StorageReference newReference=firebaseStorage.getReference(imageName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl=uri.toString();
                                    String comment=editComment.getText().toString();
                                    FirebaseUser user= auth.getCurrentUser();
                                    String email=user.getEmail();
                                    HashMap<String,Object> postData=new HashMap<>();
                                    postData.put("useremail",email);
                                    postData.put("downloadurl",downloadUrl);
                                    postData.put("comment",comment);
                                    postData.put("date", FieldValue.serverTimestamp());


                                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressAyar();
                                            Intent intent=new Intent(view.getContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);



                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(view.getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }
        });








        return view;

    }

    ActivityResultLauncher<String> mGetContext=registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result !=null){
                        imageData=result;
                        imgPost.setImageURI(result);
                    }

                }
            });
    private void progressAyar(){
        if (postProgress.isShowing())
            postProgress.dismiss();
    }




}