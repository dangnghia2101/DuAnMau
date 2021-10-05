package com.example.duanmau.Model;

public class PhieuMuon {
    private int MaPM;
    private int MaSach;
    private int MaTV;
    private int TraSach;
    private int TienThue;
    private String MaTT;
    private String Ngay;

    public PhieuMuon(int maPM, int maSach, int maTV, int traSach, int tienThue, String maTT, String ngay) {
        MaPM = maPM;
        MaSach = maSach;
        MaTV = maTV;
        TraSach = traSach;
        TienThue = tienThue;
        MaTT = maTT;
        Ngay = ngay;
    }

    public int getMaPM() {
        return MaPM;
    }

    public void setMaPM(int maPM) {
        MaPM = maPM;
    }

    public int getMaSach() {
        return MaSach;
    }

    public void setMaSach(int maSach) {
        MaSach = maSach;
    }

    public int getMaTV() {
        return MaTV;
    }

    public void setMaTV(int maTV) {
        MaTV = maTV;
    }

    public int getTraSach() {
        return TraSach;
    }

    public void setTraSach(int traSach) {
        TraSach = traSach;
    }

    public int getTienThue() {
        return TienThue;
    }

    public void setTienThue(int tienThue) {
        TienThue = tienThue;
    }

    public String getMaTT() {
        return MaTT;
    }

    public void setMaTT(String maTT) {
        MaTT = maTT;
    }

    public String getNgay() {
        return Ngay;
    }

    public void setNgay(String ngay) {
        Ngay = ngay;
    }
}
