package com.example.duanmau.Model;

public class ThanhVien {
    private int MaTV;
    private String HoTen;
    private String NamSinh;
    private String SDT;
    private String Avatar;
    private String MatKhau;

    public ThanhVien(int maTV, String hoTen, String namSinh, String SDT, String avatar, String matKhau) {
        MaTV = maTV;
        HoTen = hoTen;
        NamSinh = namSinh;
        this.SDT = SDT;
        Avatar = avatar;
        MatKhau = matKhau;
    }

    public int getMaTV() {
        return MaTV;
    }

    public void setMaTV(int maTV) {
        MaTV = maTV;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getNamSinh() {
        return NamSinh;
    }

    public void setNamSinh(String namSinh) {
        NamSinh = namSinh;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        MatKhau = matKhau;
    }
}

