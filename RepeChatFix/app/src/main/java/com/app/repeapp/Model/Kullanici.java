package com.app.repeapp.Model;

public class Kullanici {
    private String kullaniciIsmi, kullaniciEmail, kullaniciId, kullaniciProfil;
    private Boolean kullaniciOnline;

    public Kullanici(String kullaniciIsmi, String kullaniciEmail, String kullaniciId, String kullaniciProfil, Boolean kullaniciOnline) {
        this.kullaniciIsmi = kullaniciIsmi;
        this.kullaniciEmail = kullaniciEmail;
        this.kullaniciId = kullaniciId;
        this.kullaniciProfil = kullaniciProfil;
        this.kullaniciOnline = kullaniciOnline;
    }

    public Kullanici() {
    }

    public Boolean getKullaniciOnline() {
        return kullaniciOnline;
    }

    public void setKullaniciOnline(Boolean kullaniciOnline) {
        this.kullaniciOnline = kullaniciOnline;
    }

    public String getKullaniciIsmi() {
        return kullaniciIsmi;
    }

    public void setKullaniciIsmi(String kullaniciIsmi) {
        this.kullaniciIsmi = kullaniciIsmi;
    }

    public String getKullaniciEmail() {
        return kullaniciEmail;
    }

    public void setKullaniciEmail(String kullaniciEmail) {
        this.kullaniciEmail = kullaniciEmail;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public String getKullaniciProfil() {
        return kullaniciProfil;
    }

    public void setKullaniciProfil(String kullaniciProfil) {
        this.kullaniciProfil = kullaniciProfil;
    }
}
