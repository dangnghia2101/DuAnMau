package com.example.duanmau.Model;

public class Sach{
    private int MaSach;
    private String MaLoai;
    private String TenSach;
    private int SoLuong;
    private int GiaThue;
    private String Avatar;

    public Sach(int maSach, String maLoai, String tenSach, int soLuong, int giaThue, String avatar) {
        MaSach = maSach;
        MaLoai = maLoai;
        TenSach = tenSach;
        SoLuong = soLuong;
        GiaThue = giaThue;
        Avatar = avatar;
    }

    public int getMaSach() {
        return MaSach;
    }

    public void setMaSach(int maSach) {
        MaSach = maSach;
    }

    public String getMaLoai() {
        return MaLoai;
    }

    public void setMaLoai(String maLoai) {
        MaLoai = maLoai;
    }

    public String getTenSach() {
        return TenSach;
    }

    public void setTenSach(String tenSach) {
        TenSach = tenSach;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int soLuong) {
        SoLuong = soLuong;
    }

    public int getGiaThue() {
        return GiaThue;
    }

    public void setGiaThue(int giaThue) {
        GiaThue = giaThue;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }


}
