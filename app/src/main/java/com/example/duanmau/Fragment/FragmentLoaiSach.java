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
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.duanmau.Adapter.LoaiSachAdapter;
import com.example.duanmau.Adapter.ThanhVienAdapter;
import com.example.duanmau.Model.LoaiSach;
import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentLoaiSach extends Fragment {

    private SwipeMenuListView swipeMenuListView;
    private List<LoaiSach> list;
    FloatingActionButton floatingActionButton;

    EditText edt_dialogTenLoaiSach, edt_dialogThemTenLoaiSach, edt_dialogThemMaLoaiSach;
    Button btn_dialogSendSuaLoaiSach, btn_dialogSendThemLoaiSach;

    //Firestore
    FirebaseFirestore db;
    final CollectionReference reference = FirebaseFirestore.getInstance().collection("LoaiSach");


    LoaiSach loaiSach;
    View view;

    Dialog dialog_suaLoaiSach, dialog_ThemLoaiSach;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    //Image firebase
    StorageReference storageReference;


    public FragmentLoaiSach() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loai_sach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeMenuListView = view.findViewById(R.id.swlv_loaisach);
        floatingActionButton = view.findViewById(R.id.flbtn_addLoaiSach);

        getAllLoaiSach(getContext());

        //Kéo trượt swipemenulistview
        createSwipeMenu();

        //Event click swipemenu
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        dialog_suaThanhVien(position);
                        break;
                    case 1:
                        deleteLoaiSachFireBase(position);
                        break;
                }

                return false;
            }
        });

        //Thêm loại sách
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_themLoaiSach();
            }
        });

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


    //Xóa loại sách
    private void deleteLoaiSachFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa loại sách không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("LoaiSach").document(list.get(positon).getMaLoai())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getAllLoaiSach(getContext());
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


    public void getAllLoaiSach(Context context){
        list = new ArrayList<>();

        final CollectionReference reference = db.collection("LoaiSach");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaLoai = doc.get("MaLoai").toString();
                            String TenLoai = doc.get("TenLoai").toString();

                            loaiSach = new LoaiSach(MaLoai, TenLoai);
                            list.add(loaiSach);

                        }
                        LoaiSachAdapter adapter =new LoaiSachAdapter(context, list);
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

    // Cập nhật lại sách firebase
    private void updateFirebase(LoaiSach loaiSach){
        try {
            Map map = new HashMap<String, Object>();
            map.put("MaLoai", loaiSach.getMaLoai());
            map.put("TenLoai", loaiSach.getTenLoai());

            reference.document(loaiSach.getMaLoai()).set(map, SetOptions.merge());

            dialog_suaLoaiSach.dismiss();
            //Cập nhật lại listView
            getAllLoaiSach(getContext());
        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void dialog_suaThanhVien(int positon){
        dialog_suaLoaiSach =  new Dialog(getContext());
        dialog_suaLoaiSach.setContentView(R.layout.dialog_sualoaisach);

        dialog_suaLoaiSach.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        dialog_suaLoaiSach.getWindow().setLayout(width,height);

        edt_dialogTenLoaiSach = dialog_suaLoaiSach.findViewById(R.id.edt_dialogTenSuaLoaiSach);
        btn_dialogSendSuaLoaiSach = dialog_suaLoaiSach.findViewById(R.id.btn_dialogGuiSuaLoaiSach);


        // Thêm dữ liệu vào dialog sửa
        edt_dialogTenLoaiSach.setText(list.get(positon).getTenLoai());

        loaiSach = list.get(positon);

        btn_dialogSendSuaLoaiSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenLoaiSach = edt_dialogTenLoaiSach.getText().toString();

                if(tenLoaiSach.isEmpty() ){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else{
                    list.get(positon).setTenLoai(tenLoaiSach);
                    updateFirebase(loaiSach);
                }
            }
        });

        dialog_suaLoaiSach.show();
    }


    /////////////////////////////Thêm loại sách

    private void dialog_themLoaiSach(){
        dialog_ThemLoaiSach =  new Dialog(getContext());
        dialog_ThemLoaiSach.setContentView(R.layout.dialog_themloaisach);

        dialog_ThemLoaiSach.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        dialog_ThemLoaiSach.getWindow().setLayout(width,height);

        edt_dialogThemMaLoaiSach = dialog_ThemLoaiSach.findViewById(R.id.edt_dialogThemMaLoaiSach);
        edt_dialogThemTenLoaiSach = dialog_ThemLoaiSach.findViewById(R.id.edt_dialogThemTenLoaiSach);
        btn_dialogSendThemLoaiSach = dialog_ThemLoaiSach.findViewById(R.id.btn_dialogGuiThemLoaiSach);


        btn_dialogSendThemLoaiSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maLoai = edt_dialogThemMaLoaiSach.getText().toString();
                String tenLoai = edt_dialogThemTenLoaiSach.getText().toString();

                if(maLoai.isEmpty() || tenLoai.isEmpty()){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else{
                    //THêm thành viên vô database
                    loaiSach = new LoaiSach(maLoai, tenLoai);
                    db = FirebaseFirestore.getInstance();

                    int check= 0;
                    for(LoaiSach ls: list){
                        if(ls.getMaLoai().equalsIgnoreCase(loaiSach.getMaLoai())){
                            check=1;
                            break;
                        }
                    }

                    if(check==1){
                        Toast.makeText(getContext(), "Mã loại sách đã tồn tại", Toast.LENGTH_SHORT).show();
                    }else {
                        loaiSach = new LoaiSach(maLoai, tenLoai);
                        addLoaiSachToFireStore(loaiSach);
                        getAllLoaiSach(getContext());
                        dialog_ThemLoaiSach.dismiss();
                    }

                }

            }
        });

        dialog_ThemLoaiSach.show();
    }



    private void addLoaiSachToFireStore(LoaiSach loaiSach){
        //creating collection reference
        //for our Firebase FireStore database
        final CollectionReference collectionReference = db.collection("LoaiSach");

        Map<String, Object> data = new HashMap<>();
        data.put("MaLoai", loaiSach.getMaLoai());
        data.put("TenLoai", loaiSach.getTenLoai());

        try {
            collectionReference.document(loaiSach.getMaLoai()).set(data);
        }catch (Exception e){
            Log.d("Error_addTVFirebase", e.getMessage());
        }
    }




}