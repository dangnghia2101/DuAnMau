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
import android.widget.SearchView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FragPhieuMuon extends Fragment {

    private SwipeMenuListView swipeMenuListView;
    public List<PhieuMuon> list;
    FloatingActionButton floatingActionButton;
    private SearchView sv_pm;

    private Dialog dialog_themPhieuMuon;
    private Spinner sp_maSach, sp_maTT, spmaTV;
    private Button btn_dialogSubmit;
    private TextView edt_thue;


    //List load to spinner
    private List<Integer> listMaSach;
    private List<Integer> listSLSach;
    private List<String> listMaTT;
    private List<Integer> listMaTV;

    // Quy???n c???a ng?????i ????ng nh???p ???????c s??? d???ng ch???c n??ng g??
    private int Quyen = 3;

    //Ng?????i d??ng ??ang ch???n v??? tr?? spinner m?? s??ch th??? m???y, ????? ki???m tra sl s??ch trong th?? vi???n
    private int click_maSach = 0;

    //Firestore
    FirebaseFirestore db;


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
        sv_pm = view.findViewById(R.id.sv_phieuMuon);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //C??ch 2
                dialog_themPhieuMuon();
            }
        });

        //L???y danh s??ch t??? Firebase xu???ng Swipeview
        getAllPhieuMuon(getContext());
        createSwipeMenu();

        //Event click swipemenu
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        dialog_traSach(list.get(position).getMaPM()+"", list.get(position).getMaSach());
                        break;
                    case 1:

                        deletePhieuMuonFireBase(position);
                        break;
                }

                return false;
            }
        });


        // L???y danh s??ch spinner
        setMaSachToSpiner();
        setMaTTToSpiner();
        setMaTVToSpiner();

        Intent intent = getActivity().getIntent();
        Quyen = intent.getIntExtra("Quyen", 3);
        //???N t??c v???
        hide_quyen();

        //T???o k??o tr?????t listview
        createSwipeMenu();

        //T??m ki???m
        search();
    }

    private void search(){
        sv_pm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text_search = sv_pm.getQuery()+"";
                List<PhieuMuon> listPm = new ArrayList<>();

                for(PhieuMuon pm: list){
                    String maTV =String.valueOf(pm.getMaTV());
                    String maSach =String.valueOf(pm.getMaSach());
                    String maTT =String.valueOf(pm.getMaTT());

                    if(text_search.contains(maTT) || text_search.contains(maTV)){
                        listPm.add(pm);
                    }
                }

                PhieuMuonAdapter adapter =new PhieuMuonAdapter(getContext(), listPm);
                swipeMenuListView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PhieuMuonAdapter adapter =new PhieuMuonAdapter(getContext(), list);
                swipeMenuListView.setAdapter(adapter);
                return false;
            }
        });
    }

    private void dialog_traSach(String MaPM, int maSach){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Th??ng b??o")
                .setMessage("B???n ch???n ch???n mu???n tr??? s??ch kh??ng?")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Toast.makeText(getContext(), "???? tr??? s??ch", Toast.LENGTH_SHORT).show();
                            db.collection("PHIEUMUON").document(MaPM).update("TraSach", 1);
                            updateSach(1, maSach);
                            getAllPhieuMuon(getContext());
                        }catch (Exception e){
                            Toast.makeText(getContext(), "Kh??ng ?????i ???????c m???t kh???u, l???i "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    public void createSwipeMenu(){
        // K??o ngang Swipemenu
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
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Erroout", e.getMessage());
                }
            }
        });
    }

    private void deletePhieuMuonFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Th??ng b??o")
                .setMessage("B???n ch???n ch???n mu???n x??a s??ch kh??ng?")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
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
                }).setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    //Dialog th??m phi???u m?????n

    private void dialog_themPhieuMuon() {
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

        //Adapter v??o spinner
        setListAdapterSpinner();

        //L???y d??? li???u ra t??? spinner
        //Toast.makeText(getContext(), getMaTTSpinner() + " " + getMaSachSpinner() + " " + getMaTVSpinner(), Toast.LENGTH_LONG).show();

        btn_dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), masach[0] + " " + maTT[0] + " " + maTV[0], Toast.LENGTH_LONG).show();
                Random random = new Random();
                int numRandom = random.nextInt(20000) + 1;
                int tienThue = Integer.parseInt(edt_thue.getText().toString());

                phieuMuon = new PhieuMuon(numRandom, masach[0], maTV[0], 0, tienThue, maTT[0], "");
                //Ki???m tra th??nh vi??n n??y c?? m?????n cu???n s??ch n??o ch??a
                check_phieuMuonTonTai(phieuMuon.getMaTV(), phieuMuon.getMaSach());
            }
        });


        //spinner m?? s??ch
        masach[0] = listMaSach.get(0);
        //L???y gi?? s??ch
        getGiaSach(masach[0]);
        sp_maSach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                masach[0] = listMaSach.get(position);
                getGiaSach(masach[0]);

                //G??n v??? tr?? cho bi???n ????? l???y ra s??? l?????ng c???a s??ch
                click_maSach = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spiner th??? th??
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

        // List m?? th??nh vi??n
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

    //Ki???m tra ng?????i d??ng ???? m?????n cu???n s??ch n??o ch??a
    private void check_phieuMuonTonTai(int _maTV, int _maSach){

        //Ki???m tra c??n s??ch trong th?? vi???n kh??ng
        if(listSLSach.get(click_maSach) <= 0){
            Toast.makeText(getContext(), "Xin l???i, s??ch trong th?? vi???n ???? h???t. Vui l??ng ch???n s??ch kh??c", Toast.LENGTH_SHORT).show();
            return;
        }

        final CollectionReference reference = db.collection("PHIEUMUON");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaTV = Integer.parseInt(doc.get("MaTV").toString());
                            int MaSach = Integer.parseInt(doc.get("MaSach").toString());

                            if(_maSach == MaSach && _maTV == MaTV){
                                Toast.makeText(getContext(), "Th??nh vi??n n??y ???? m?????n m???t cu???n t????ng t???", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        themPhieuMuon(phieuMuon);
                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return;
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
                            int SoLuong = Integer.parseInt(doc.get("SoLuong").toString());
                            listMaSach.add(MaSach);
                            listSLSach.add(SoLuong);
                        }
                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // set Adapter list l??n spinner
    private void setListAdapterSpinner(){
        //Spinner th??nh vi??n
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(),
                android.R.layout.simple_list_item_1, listMaTV);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spmaTV.setAdapter(adapter);

        //Spinner th??? th??
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, listMaTT);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_maTT.setAdapter(adapter1);


        //Sinner m?? s??ch
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(getContext(),
                android.R.layout.simple_list_item_1, listMaSach);
        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_maSach.setAdapter(adapter2);
    }

    //L???y gi?? s??ch t??? m?? s??ch
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

        updateSach(-1, pm.getMaSach());
        try {
            collectionReference.document(pm.getMaPM() + "").set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    getAllPhieuMuon(getContext());
                    Toast.makeText(getContext(), "Th??m phi???u m?????n th??nh c??ng", Toast.LENGTH_SHORT).show();
                    setMaSachToSpiner();
                }
            });

            dialog_themPhieuMuon.dismiss();

        }catch (Exception e){
            Log.d("Error_addTVFirebase", e.getMessage());
        }

    }

    //C???p nh???t s??? l?????ng s??ch s??ch trong database s??ch
    private void updateSach(int bien, int maSach){
        DocumentReference docRef = db.collection("Sach").document(maSach+"");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int soLuong = Integer.parseInt(document.get("SoLuong").toString());
                        //C???p nh???t l???i s??? l?????ng s??ch
                        db.collection("Sach").document(maSach+"").update("SoLuong", soLuong+bien);
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    //???n t??c v??? cho t???ng ng?????i d??ng
    private void hide_quyen(){
        if(Quyen == 3) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }


}