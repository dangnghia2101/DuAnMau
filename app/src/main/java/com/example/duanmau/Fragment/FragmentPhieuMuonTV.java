package com.example.duanmau.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FragmentPhieuMuonTV extends Fragment {
    Spinner spinner;
    TextView tv_tien;
    ListView listView;

    private Button btn_dialogSubmit;

    final Integer[] masach = new Integer[1];

    //List load to spinner
    private List<Integer> listMaSach;

    private List<String> listPM;

    private String maSachChon;
    private String giaSachChon;

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
        return inflater.inflate(R.layout.fragment_phieu_muon_t_v, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhxa(view);

        setMaSachToSpiner();

        click_masach();

        listPM = new ArrayList<>();

        btn_dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPM.add("Mã sách: "+maSachChon + " - Giá thuê: " + giaSachChon);

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listPM);
                listView.setAdapter(adapter);
            }
        });


    }

    private void anhxa(View view){
        spinner = view.findViewById(R.id.sp_maSachThemPMTV);
        tv_tien = view.findViewById(R.id.edt_dialogThueThemPMTV);
        btn_dialogSubmit = view.findViewById(R.id.btn_dialogGuiThemPMTV);
        listView = view.findViewById(R.id.lv_fragmentPMTV);
    }

    private void click_masach(){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                masach[0] = listMaSach.get(position);

                getGiaSach(masach[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Lấy giá sách từ mã sách
    private void getGiaSach(int maSach){
        DocumentReference docRef = db.collection("Sach").document(maSach+"");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        tv_tien.setText(document.get("GiaThue").toString());

                        //Cập nhật HN
                        maSachChon = maSach+"";
                        giaSachChon = document.get("GiaThue").toString();

                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setMaSachToSpiner(){

        listMaSach = new ArrayList<>();

        final CollectionReference reference = db.collection("Sach");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaSach = Integer.parseInt(doc.get("MaSach").toString());
                            listMaSach.add(MaSach);
                        }
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    getMaSachToSpinner();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMaSachToSpinner(){

        //Sinner mã sách
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(getContext(),
                android.R.layout.simple_list_item_1, listMaSach);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        //spinner mã sách
        masach[0] = listMaSach.get(0);

        //Lấy giá sách
        getGiaSach(masach[0]);
    }
}