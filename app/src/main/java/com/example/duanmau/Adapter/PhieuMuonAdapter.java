package com.example.duanmau.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.duanmau.Model.PhieuMuon;
import com.example.duanmau.R;

import java.util.List;

public class PhieuMuonAdapter extends BaseAdapter {
    private Context context;
    private List<PhieuMuon> list;

    public PhieuMuonAdapter(Context context, List<PhieuMuon> list) {
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
        TextView tvMaPM, tvMaTV, tvMaSach, tvMaTT, tvNgay, tvTraSach, tvTienThue;

        public ViewHolder(TextView tvMaPM, TextView tvMaTV, TextView tvMaSach, TextView tvMaTT, TextView tvNgay, TextView tvTraSach, TextView tvTienThue) {
            this.tvMaPM = tvMaPM;
            this.tvMaTV = tvMaTV;
            this.tvMaSach = tvMaSach;
            this.tvMaTT = tvMaTT;
            this.tvNgay = tvNgay;
            this.tvTraSach = tvTraSach;
            this.tvTienThue = tvTienThue;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = View.inflate(context, R.layout.item_phieumuon, null);
            TextView tvMaPM = view.findViewById(R.id.tv_MaPMItemPhieuMuon);
            TextView tvMaTV = view.findViewById(R.id.tv_MaTVItemPhieuMuon);
            TextView tvMaSach = view.findViewById(R.id.tv_MaSachItemPhieuMuon);
            TextView tvMaTT = view.findViewById(R.id.tv_MaTTItemPhieuMuon);
            TextView tvNgay = view.findViewById(R.id.tv_NgayItemPhieuMuon);
            TextView tvTraSach = view.findViewById(R.id.tv_TraSachItemPhieuMuon);
            TextView tvTienThue = view.findViewById(R.id.tv_TienThueItemPhieuMuon);

            ViewHolder holder = new ViewHolder(tvMaPM, tvMaTV, tvMaSach, tvMaTT, tvNgay, tvTraSach, tvTienThue);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        PhieuMuon pm = list.get(position);
        holder.tvMaPM.setText(pm.getMaPM()+"");
        holder.tvMaSach.setText(pm.getMaSach()+"");
        holder.tvMaTT.setText(pm.getMaTT());
        holder.tvMaTV.setText(pm.getMaTV()+"");
        holder.tvNgay.setText(pm.getNgay());
        holder.tvTienThue.setText(pm.getTienThue()+"");
        holder.tvTraSach.setText(pm.getTraSach()+"");

        return view;
    }
}
