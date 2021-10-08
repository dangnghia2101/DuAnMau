package com.example.duanmau.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.duanmau.Adapter.PhieuMuonAdapter;
import com.example.duanmau.Adapter.SachAdapter;
import com.example.duanmau.Model.LoaiSach;
import com.example.duanmau.Model.PhieuMuon;
import com.example.duanmau.Model.Sach;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FragPhieuMuon extends Fragment {

    private SwipeMenuListView swipeMenuListView;
    public List<PhieuMuon> list;
    FloatingActionButton floatingActionButton;

    private Dialog dialog_themPhieuMuon;
    private Spinner sp_maSach, sp_maTT, spmaTV;
    private Button btn_dialogSubmit;
    private TextView edt_thue;

    //List load to spinner
    private List<Integer> listMaSach;
    private List<String> listMaTT;
    private List<Integer> listMaTV;

    // Quyền của người đăng nhập được sử dụng chức năng gì
    private int Quyen = 3;

    //Firestore
    FirebaseFirestore db;
    final CollectionReference reference = FirebaseFirestore.getInstance().collection("PHIEUMUON");


    PhieuMuon phieuMuon;
    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_phieu_muon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeMenuListView = view.findViewById(R.id.swlv_phieuMuon);
        floatingActionButton = view.findViewById(R.id.flbtn_addPhieuMuon);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cách 2
                dialog_themLoaiSach();
            }
        });

        //Lấy danh sách từ Firebase xuống Swipeview
        getAllPhieuMuon(getContext());
        createSwipeMenu();

        //Event click swipemenu
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        dialog_traSach(list.get(position).getMaPM()+"");
                        break;
                    case 1:

                        deletePhieuMuonFireBase(position);
                        break;
                }

                return false;
            }
        });


        // Lấy danh sách spinner
        setMaSachToSpiner();
        setMaTTToSpiner();
        setMaTVToSpiner();

        Intent intent = getActivity().getIntent();
        Quyen = intent.getIntExtra("Quyen", 3);
        //ẨN tác vụ
        hide_quyen();

        //Tạo kéo trượt listview
        createSwipeMenu();
    }

    private void dialog_traSach(String MaPM){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn trả sách không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Toast.makeText(getContext(), "Đã trả sách", Toast.LENGTH_SHORT).show();
                            db.collection("PHIEUMUON").document(MaPM).update("TraSach", 1);
                            getAllPhieuMuon(getContext());
                        }catch (Exception e){
                            Toast.makeText(getContext(), "Không đổi được mật khẩu, lỗi "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    public void createSwipeMenu(){
        // Kéo ngang Swipemenu
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0x33, 0x99,
                        0xCE)));
                // set item width
                openItem.setWidth(250);

                // set item title
                //openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.BLUE);
                //set a icon
                openItem.setIcon(R.drawable.icon_detail);

                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0x33,
                        0x99, 0xCE)));
                // set item width
                deleteItem.setWidth(250);
                // set a icon
                deleteItem.setIcon(R.drawable.icon_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        swipeMenuListView.setMenuCreator(creator);
    }

    public void getAllPhieuMuon(Context context){
        list = new ArrayList<>();

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
                            list.add(phieuMuon);

                        }
                        PhieuMuonAdapter adapter =new PhieuMuonAdapter(context, list);
                        swipeMenuListView.setAdapter(adapter);
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletePhieuMuonFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa sách không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("PHIEUMUON").document(list.get(positon).getMaPM()+"")
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getAllPhieuMuon(getContext());
                            }
                        });


                    }
                }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    //Dialog thêm phiếu mượn

    private void dialog_themLoaiSach() {
        final Integer[] masach = new Integer[1];
        final String[] maTT = new String[1];
        final Integer[] maTV = new Integer[1];

        dialog_themPhieuMuon = new Dialog(getContext());
        dialog_themPhieuMuon.setContentView(R.layout.dialog_themphieumuon);

        edt_thue = dialog_themPhieuMuon.findViewById(R.id.edt_dialogThueThemPM);
        sp_maSach = dialog_themPhieuMuon.findViewById(R.id.sp_maSachThemPM);
        sp_maTT = dialog_themPhieuMuon.findViewById(R.id.sp_maTTThemPM);
        spmaTV = dialog_themPhieuMuon.findViewById(R.id.sp_maTVThemPM);
        btn_dialogSubmit = dialog_themPhieuMuon.findViewById(R.id.btn_dialogGuiThemPM);

        //Adapter vào spinner
        setListAdapterSpinner();

        //Lấy dữ liệu ra từ spinner
        //Toast.makeText(getContext(), getMaTTSpinner() + " " + getMaSachSpinner() + " " + getMaTVSpinner(), Toast.LENGTH_LONG).show();

        btn_dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), masach[0] + " " + maTT[0] + " " + maTV[0], Toast.LENGTH_LONG).show();
                Random random = new Random();
                int numRandom = random.nextInt(20000) + 1;
                int tienThue = Integer.parseInt(edt_thue.getText().toString());

                phieuMuon = new PhieuMuon(numRandom, masach[0], maTV[0], 0, tienThue, maTT[0], "");
                themPhieuMuon(phieuMuon);
            }
        });


        //spinner mã sách
        masach[0] = listMaSach.get(0);
        //Lấy giá sách
        getGiaSach(masach[0]);
        sp_maSach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                masach[0] = listMaSach.get(position);

                getGiaSach(masach[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spiner thủ thư
        maTT[0] = listMaTT.get(0);
        sp_maTT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maTT[0] = listMaTT.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // List mã thành viên
        maTV[0] = listMaTV.get(0);
        spmaTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maTV[0] = listMaTV.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        dialog_themPhieuMuon.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
        dialog_themPhieuMuon.getWindow().setLayout(width, height);

        dialog_themPhieuMuon.show();
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
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setMaTTToSpiner(){

        listMaTT = new ArrayList<>();

        final CollectionReference reference = db.collection("ThuThu");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaTT = doc.get("MaTT").toString();
                            listMaTT.add(MaTT);
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


    private void setMaTVToSpiner(){

        listMaTV = new ArrayList<>();

        final CollectionReference reference = db.collection("ThanhVien");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaTV = Integer.parseInt(doc.get("MaTV").toString());
                            listMaTV.add(MaTV);
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

    // set Adapter list lên spinner
    private void setListAdapterSpinner(){
        //Spinner thành viên
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(),
                android.R.layout.simple_list_item_1, listMaTV);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spmaTV.setAdapter(adapter);

        //Spinner thủ thư
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listMaTT);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_maTT.setAdapter(adapter1);


        //Sinner mã sách
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(getContext(),
                android.R.layout.simple_list_item_1, listMaSach);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_maSach.setAdapter(adapter2);
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
                        edt_thue.setText(document.get("GiaThue").toString());
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    /// Theem phie muon
    private void themPhieuMuon(PhieuMuon pm){

        //creating collection reference
        //for our Firebase FireStore database
        final CollectionReference collectionReference = db.collection("PHIEUMUON");

        Map<String, Object> data = new HashMap<>();
        data.put("MaPM", pm.getMaPM());
        data.put("MaSach", pm.getMaSach());
        data.put("MaTT", pm.getMaTT());
        data.put("MaTV", pm.getMaTV());
        data.put("Ngay", FieldValue.serverTimestamp());
        data.put("TienThue", pm.getTienThue());
        data.put("TraSach", 0);

        try {
            collectionReference.document(pm.getMaPM() + "").set(data);
            Toast.makeText(getContext(), "Thêm phiếu mượn thành công", Toast.LENGTH_SHORT).show();
            dialog_themPhieuMuon.dismiss();
            getAllPhieuMuon(getContext());
        }catch (Exception e){
            Log.d("Error_addTVFirebase", e.getMessage());
        }

    }

    //Ẩn tác vụ cho từng người dùng
    private void hide_quyen(){
        if(Quyen == 3) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

}