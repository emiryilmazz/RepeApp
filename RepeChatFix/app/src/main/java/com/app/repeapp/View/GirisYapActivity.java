package com.app.repeapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.app.repeapp.R;

public class GirisYapActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout mLinear;
    private TextInputLayout inputEmail, inputSifre;
    private EditText editEmail, editSifre;
    private String txtEmail, txtSifre;

    private void init(){
        mLinear = (LinearLayout)findViewById(R.id.giris_yap_linear);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        inputEmail = (TextInputLayout)findViewById(R.id.giris_yap_inputEmail);
        inputSifre = (TextInputLayout)findViewById(R.id.giris_yap_inputSifre);

        editEmail = (EditText)findViewById(R.id.giris_yap_editEmail);
        editSifre = (EditText)findViewById(R.id.giris_yap_editSifre);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_yap);
        init();

        if (mUser != null){
            finish();
            startActivity(new Intent(GirisYapActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    public void btnGirisYap(View v){
        txtEmail = editEmail.getText().toString();
        txtSifre = editSifre.getText().toString();

        if (!TextUtils.isEmpty(txtEmail)){
            if (!TextUtils.isEmpty(txtSifre)){
                mProgress = new ProgressDialog(this);
                mProgress.setTitle("Giriş Yapılıyor...");
                mProgress.show();

                mAuth.signInWithEmailAndPassword(txtEmail, txtSifre)
                        .addOnCompleteListener(GirisYapActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressAyar();
                                    Toast.makeText(GirisYapActivity.this, "Başarıyla Giriş Yaptınız.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(GirisYapActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }else {
                                    progressAyar();
                                    Snackbar.make(mLinear, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else
                inputSifre.setError("Lütfen Geçerli Bir Şifre Giriniz.");
        }else
            inputEmail.setError("Lütfen Geçerli Bir Email Adresi Giriniz.");
    }

    public void btnGitKayitOl(View v){
        startActivity(new Intent(GirisYapActivity.this, KayitOlActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void progressAyar(){
        if (mProgress.isShowing())
            mProgress.dismiss();
    }
}