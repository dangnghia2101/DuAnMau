package com.example.duanmau.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duanmau.Model.Sach;
import com.example.duanmau.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SachTop10Adapter extends BaseAdapter {
    Context context;
    List<Sach> list;

    public SachTop10Adapter(Context context, List<Sach> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView tvTenLoaiSach, tvTenSach, tvSoLuong, tvGia;
        ImageView imgSach;

        public ViewHolder(TextView tvTenLoaiSach, TextView tvTenSach, TextView tvSoLuong, TextView tvGia, ImageView imgSach) {
            this.tvTenLoaiSach = tvTenLoaiSach;
            this.tvTenSach = tvTenSach;
            this.tvSoLuong = tvSoLuong;
            this.tvGia = tvGia;
            this.imgSach = imgSach;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = View.inflate(context, R.layout.item_sachtop10, null);
            TextView tvTenLoaiSach = view.findViewById(R.id.tv_tenLoaiItemSachtop10);
            TextView tvTenSach = view.findViewById(R.id.tv_NameItemSachtop10);
            TextView tvSoLuong = view.findViewById(R.id.tv_soLuongItemSachtop10);
            TextView tvGia = view.findViewById(R.id.tv_giaItemSachtop10);
            ImageView imgAva = view.findViewById(R.id.imv_avatarItemSachtop10);

            SachTop10Adapter.ViewHolder holder = new SachTop10Adapter.ViewHolder(tvTenLoaiSach, tvTenSach, tvSoLuong, tvGia, imgAva);
            view.setTag(holder);
        }

        SachTop10Adapter.ViewHolder holder = (SachTop10Adapter.ViewHolder) view.getTag();
        Sach sach = list.get(position);
        holder.tvTenSach.setText(sach.getTenSach());
        holder.tvTenLoaiSach.setText(sach.getMaLoai());
        holder.tvGia.setText(sach.getGiaThue()+"");
        holder.tvSoLuong.setText(sach.getSoLuong()+"");
        if(sach.getAvatar().isEmpty()){
            holder.imgSach.setImageResource(R.drawable.ic_baseline_person_24);
        }else{
            Picasso.get().load(sach.getAvatar()).into(holder.imgSach);
        }

        return view;
    }
}
