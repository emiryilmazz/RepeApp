package com.app.repeapp.Model;

public class MesajIstegi {
    private String kanalId;
    private String kullaniciId;
    private String kullaniciIsim;
    private String kullaniciProfil;

    public MesajIstegi(String kanalId, String kullaniciId, String kullaniciIsim, String kullaniciProfil) {
        this.kanalId = kanalId;
        this.kullaniciId = kullaniciId;
        this.kullaniciIsim = kullaniciIsim;
        this.kullaniciProfil = kullaniciProfil;
    }

    public MesajIstegi() {
    }

    public String getKullaniciProfil() {
        return kullaniciProfil;
    }

    public void setKullaniciProfil(String kullaniciProfil) {
        this.kullaniciProfil = kullaniciProfil;
    }

    public String getKullaniciIsim() {
        return kullaniciIsim;
    }

    public void setKullaniciIsim(String kullaniciIsim) {
        this.kullaniciIsim = kullaniciIsim;
    }

    public String getKanalId() {
        return kanalId;
    }

    public void setKanalId(String kanalId) {
        this.kanalId = kanalId;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }
}
