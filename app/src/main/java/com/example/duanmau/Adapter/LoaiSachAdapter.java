package com.example.duanmau.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duanmau.Model.LoaiSach;
import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoaiSachAdapter extends BaseAdapter {
    private Context context;
    private List<LoaiSach> list;

    public LoaiSachAdapter(Context context, List<LoaiSach> list) {
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
        TextView tvMaLoaiSach, tvTenLoaiSach;

        public ViewHolder(TextView tvMaLoaiSach, TextView tvTenLoaiSach) {
            this.tvMaLoaiSach = tvMaLoaiSach;
            this.tvTenLoaiSach = tvTenLoaiSach;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = View.inflate(context, R.layout.item_loaisach, null);
            TextView tvMaLS = (TextView) view.findViewById(R.id.tv_maItemLoaiSach);
            TextView tvTenLS = (TextView) view.findViewById(R.id.tv_NameItemLoaiSach);


            ViewHolder viewHolder = new ViewHolder(tvMaLS, tvTenLS);
            view.setTag(viewHolder);
        }

        LoaiSachAdapter.ViewHolder holder = (LoaiSachAdapter.ViewHolder) view.getTag();

        LoaiSach loaiSach = list.get(position);
        holder.tvMaLoaiSach.setText(loaiSach.getMaLoai());
        holder.tvTenLoaiSach.setText(loaiSach.getTenLoai());

        return view;
    }
}
