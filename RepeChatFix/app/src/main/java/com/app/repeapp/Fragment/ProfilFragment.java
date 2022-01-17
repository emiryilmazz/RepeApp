package com.app.repeapp.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.repeapp.View.GirisYapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.app.repeapp.Model.Kullanici;
import com.app.repeapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfilFragment extends Fragment {
    private static final int IZIN_KODU = 0;
    private static final int IZIN_ALINDI_KODU = 1;

    private EditText editIsim, editEmail;
    private CircleImageView imgProfil;
    private ImageView imgYeniResim;
    private View v;
    private Button signOut;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private DocumentReference mRef;
    private FirebaseUser mUser;
    private Kullanici user;
    private Fragment profilFragment;
    private Intent galeriIntent;
    private Uri mUri;
    private Bitmap gelenResim;
    private ImageDecoder.Source imgSource;
    private ByteArrayOutputStream outputStream;
    private byte[] imgByte;
    private StorageReference mStorageRef, yeniRef, sRef;
    private String kayitYeri, indirmeLinki;
    private HashMap<String, Object> mData;
    private ProgressDialog progressDialog;

    private Query mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profil, container, false);

        editIsim = v.findViewById(R.id.profil_fragment_editIsim);
        editEmail = v.findViewById(R.id.profil_fragment_editEmail);
        imgProfil = v.findViewById(R.id.profil_fragment_imgUserProfil);
        imgYeniResim = v.findViewById(R.id.profil_fragment_imgYeniResim);
        signOut=v.findViewById(R.id.signOutButton);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        profilFragment=new ProfilFragment();


        mRef = mFireStore.collection("Kullanıcılar").document(mUser.getUid());
        mRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null && value.exists()) {
                    user = value.toObject(Kullanici.class);

                    if (user != null) {
                        editIsim.setText("Ad Soyad : "+user.getKullaniciIsmi());
                        editEmail.setText("Eposta : "+user.getKullaniciEmail());

                        if (user.getKullaniciProfil().equals("default"))
                            imgProfil.setImageResource(R.mipmap.ic_launcher);
                        else
                            Picasso.get().load(user.getKullaniciProfil()).resize(156, 156).into(imgProfil);
                    }
                }
            }
        });

        imgYeniResim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IZIN_KODU);
                else
                    galeriyeGit();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent anaekran=new Intent(v.getContext(),GirisYapActivity.class);

                startActivity(anaekran);
            }
        });




        return v;
    }

    private void galeriyeGit() {
        galeriIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeriIntent, IZIN_ALINDI_KODU);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IZIN_KODU) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                galeriyeGit();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IZIN_ALINDI_KODU) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                mUri = data.getData();

                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        imgSource = ImageDecoder.createSource(v.getContext().getContentResolver(), mUri);
                        gelenResim = ImageDecoder.decodeBitmap(imgSource);
                    } else {
                        gelenResim = MediaStore.Images.Media.getBitmap(v.getContext().getContentResolver(), mUri);
                    }

                    outputStream = new ByteArrayOutputStream();
                    gelenResim.compress(Bitmap.CompressFormat.PNG, 75, outputStream);
                    imgByte = outputStream.toByteArray();
                    progressDialog= new ProgressDialog(getContext());
                    progressDialog.setTitle("Yükleniyor...");
                    progressDialog.show();

                    kayitYeri = "Kullanicilar/" + user.getKullaniciEmail() + "/profil.png";
                    sRef = mStorageRef.child(kayitYeri);
                    sRef.putBytes(imgByte)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    yeniRef = FirebaseStorage.getInstance().getReference(kayitYeri);
                                    yeniRef.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    indirmeLinki = uri.toString();
                                                    mData = new HashMap<>();
                                                    mData.put("kullaniciProfil", indirmeLinki);
                                                    progressAyar();

                                                    mFireStore.collection("Kullanıcılar").document(mUser.getUid())
                                                            .update(mData)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                        iletisimIcinProfilGuncelle(indirmeLinki);

                                                                    else
                                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void iletisimIcinProfilGuncelle(final String link) {
        mQuery = mFireStore.collection("Kullanıcılar").document(mUser.getUid()).collection("Kanal");
        mQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot snp : queryDocumentSnapshots.getDocuments()) {
                                mData = new HashMap<>();
                                mData.put("kullaniciProfil", link);

                                mFireStore.collection("Kullanıcılar").document(snp.getData().get("kullaniciId").toString()).collection("Kanal").document(mUser.getUid())
                                        .update(mData);
                            }

                            Toast.makeText(v.getContext(), "Profil Resminiz Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void progressAyar(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}