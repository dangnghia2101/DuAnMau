package com.example.duanmau.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duanmau.Adapter.PhieuMuonAdapter;
import com.example.duanmau.Model.PhieuMuon;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class FragmentDoanhThu extends Fragment {
    EditText edt_ngayDau, edt_ngayCuoi;
    TextView tv_doanhThu;

    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;

    //Firestore
    FirebaseFirestore db;
    final CollectionReference reference = FirebaseFirestore.getInstance().collection("PHIEUMUON");

    private String ngayDau, ngayCuoi;
    private int tongDoanhThu = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doanh_thu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhxa(view);

        edt_ngayDau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonNgayDatePicker(0);
            }
        });

        edt_ngayCuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonNgayDatePicker(1);

                Log.d("======>", tongDoanhThu+"");
            }
        });


    }

    private void anhxa(View view){
        edt_ngayDau = view.findViewById(R.id.edt_fragNgayBatDauDoanhThu);
        edt_ngayCuoi = view.findViewById(R.id.edt_fragNgayKetThucDoanhThu);
        tv_doanhThu = view.findViewById(R.id.tv_fragDoanhThu);
    }

    private void chonNgayDatePicker(int vitri){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String day = String.valueOf(dayOfMonth);
                String mon = String.valueOf(month+1);

                if(dayOfMonth<10) day = "0"+day;
                if(month<9) mon = "0"+mon;

                String kq = day + "-" + mon + "-" + year;

                if(vitri == 0){
                    edt_ngayDau.setText(kq);
                    ngayDau = kq;
                }else {
                    edt_ngayCuoi.setText(kq);
                    ngayCuoi = kq;
                    getFirestorePhieuMuon();
                };
            }
        };

        DatePickerDialog datePickerDialog = null;

        // Create DatePickerDialog:
        datePickerDialog = new DatePickerDialog(getContext(),
                dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);


        // Show
        datePickerDialog.show();
    }


    public void getFirestorePhieuMuon(){
        final CollectionReference reference = db.collection("PHIEUMUON");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int TienThue = Integer.parseInt(doc.get("TienThue").toString());
                            int TraSach = Integer.parseInt(doc.get("TraSach").toString());
                            Timestamp ngay = (Timestamp) doc.get("Ngay");

                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String ngayMuon = format.format(ngay.toDate());

                            if (TraSach == 1) getDoanhThu(ngayDau, ngayCuoi, ngayMuon, TienThue);
                        }

                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDoanhThu(String d1, String d2, String dFire, int tienThue){
        if(dFire.compareTo(d2) < 0 && dFire.compareTo(d1)>0) {
            tongDoanhThu += tienThue;
        }
        tv_doanhThu.setText(formatNumber(tongDoanhThu) + " VNĐ");
    }

    private String formatNumber(int number){
        // tạo 1 NumberFormat để định dạng số theo tiêu chuẩn của nước Anh
        Locale localeEN = new Locale("en", "EN");
        NumberFormat en = NumberFormat.getInstance(localeEN);

        return en.format(number);
    }
}


