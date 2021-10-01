package com.example.duanmau.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ThanhVienAdapter extends BaseAdapter {
    private Context context;
    private List<ThanhVien> list;

    public ThanhVienAdapter(Context context, List<ThanhVien> list) {
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
        TextView tvTenTV, tvPhoneTV,tvMaTv;
        ImageView imgAvatar;

        public ViewHolder(TextView tvTenTV, TextView tvPhoneTV, TextView tvMaTv, ImageView imgAvatar) {
            this.tvTenTV = tvTenTV;
            this.tvPhoneTV = tvPhoneTV;
            this.tvMaTv = tvMaTv;
            this.imgAvatar = imgAvatar;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){

            view = View.inflate(context, R.layout.item_thanhvien, null);
            TextView tvTenTV = (TextView) view.findViewById(R.id.tv_NameItemThanhVien);
            TextView tvPhoneTV = (TextView) view.findViewById(R.id.tv_phoneItemThanhVien);
            TextView tvMaTv = (TextView) view.findViewById(R.id.tv_maItemThanhVien);
            ImageView imgAvatar = (ImageView) view.findViewById(R.id.imv_avatarItemThanhVien);

            ViewHolder viewHolder = new ViewHolder(tvTenTV, tvPhoneTV, tvMaTv, imgAvatar);
            view.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) view.getTag();

        ThanhVien thanhVien = list.get(position);
        holder.tvTenTV.setText(thanhVien.getHoTen());
        holder.tvPhoneTV.setText(thanhVien.getSDT());
        holder.tvMaTv.setText(thanhVien.getMaTV()+"");
        if(thanhVien.getAvatar().isEmpty()){
            holder.imgAvatar.setImageResource(R.drawable.ic_baseline_person_24);
        }else{
            Picasso.get().load(thanhVien.getAvatar()).into(holder.imgAvatar);
        }

        return view;
    }
}
