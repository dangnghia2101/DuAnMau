package com.example.duanmau.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.duanmau.Adapter.PhieuMuonAdapter;
import com.example.duanmau.Adapter.SachAdapter;

import com.example.duanmau.Adapter.SachTop10Adapter;
import com.example.duanmau.Model.PhieuMuon;
import com.example.duanmau.Model.Sach;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FragmenDt1top10 extends Fragment {
    public List<PhieuMuon> listPM;
    public List<Sach> listSach;

    private Sach sach;
    private PhieuMuon phieuMuon;

    ListView lv;

    //Firestore
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmen_dt1top10, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.lv_top10);

        addListSach();
    }

    private void addListSach(){
        listSach = new ArrayList<>();

        final CollectionReference reference = db.collection("Sach");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaSach = Integer.parseInt(doc.get("MaSach").toString());
                            String MaLoai = doc.get("MaLoai").toString();
                            String TenSach = doc.get("TenSach").toString();
                            int GiaThue = Integer.parseInt(doc.get("GiaThue").toString());
                            int SoLuong = Integer.parseInt(doc.get("SoLuong").toString());
                            String Avatar = doc.get("Avatar").toString();

                            sach = new Sach(MaSach, MaLoai, TenSach, 0, GiaThue, Avatar);
                            listSach.add(sach);
                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    addListPM();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addListPM(){
        listPM = new ArrayList<>();

        final CollectionReference reference = db.collection("PHIEUMUON");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaSach = Integer.parseInt(doc.get("MaSach").toString());
                            int MaPM = Integer.parseInt(doc.get("MaPM").toString());
                            int MaTV = Integer.parseInt(doc.get("MaTV").toString());
                            int TienThue = Integer.parseInt(doc.get("TienThue").toString());
                            int TraSach = Integer.parseInt(doc.get("TraSach").toString());
                            String MaTT = doc.get("MaTT").toString();
                            Timestamp ngay = (Timestamp) doc.get("Ngay");

                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a");
                            String ngayMuon = format.format(ngay.toDate());

                            phieuMuon = new PhieuMuon(MaPM, MaSach, MaTV, TraSach, TienThue, MaTT, ngayMuon);
                            listPM.add(phieuMuon);

                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    tinhDoanhThu();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void tinhDoanhThu() {
        for(PhieuMuon pm: listPM){
            for(Sach sach: listSach){
                if(pm.getMaSach() == sach.getMaSach() && pm.getTraSach()==1){
                    sach.setSoLuong(sach.getSoLuong() + 1);
                    break;
                }
            }
        }

        for(int i=0; i<listSach.size(); i++){
            if(listSach.get(i).getSoLuong()==0){
                listSach.remove(i);
                i--;
            }

        }

        Collections.sort(listSach, new Comparator<Sach>() {
            @Override
            public int compare(Sach o1, Sach o2) {
                return String.valueOf(o2.getSoLuong()).compareTo(String.valueOf(o1.getSoLuong()));
            }
        });

        SachTop10Adapter adapter = new SachTop10Adapter(getContext(), listSach);
        lv.setAdapter(adapter);
    }
}