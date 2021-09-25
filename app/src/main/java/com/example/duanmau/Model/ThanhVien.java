package com.example.duanmau.Model;

public class ThanhVien {
    private int MaTV;
    private String HoTen;
    private String NamSinh;
    private String SDT;

    public ThanhVien(int maTV, String hoTen, String namSinh, String SDT) {
        MaTV = maTV;
        HoTen = hoTen;
        NamSinh = namSinh;
        this.SDT = SDT;
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
}
