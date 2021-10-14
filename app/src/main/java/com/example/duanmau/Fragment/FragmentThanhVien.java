package com.example.duanmau.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.duanmau.Adapter.ThanhVienAdapter;
import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentThanhVien#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentThanhVien extends Fragment {
    private SwipeMenuListView swipeMenuListView;
    public List<ThanhVien> list;
    FloatingActionButton floatingActionButton;

    EditText edt_dialogTenTV, edt_dialogNamSinhTV, edt_dialogMKTV;
    ImageButton imgBtn_dialogAvatarSuaTV;
    Button btn_dialogSendSuaTV;

    private RadioButton rbn_thanhVien, rbn_thuThu;

    //Firestore
    FirebaseFirestore db;
    final CollectionReference reference = FirebaseFirestore.getInstance().collection("ThanhVien");


    ThanhVien thanhVien;
    View view;

    Dialog dialog_suaThanhVien;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    //Image firebase
    StorageReference storageReference;

    //public FirebaseFirestore firestore;

    public FragmentThanhVien() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentThanhVien newInstance(String param1, String param2) {
        FragmentThanhVien fragment = new FragmentThanhVien();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_thanh_vien, container, false);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingActionButton = view.findViewById(R.id.flbtn_addThanhVien);
        rbn_thanhVien = view.findViewById(R.id.rbt_FragThanhVien);
        rbn_thanhVien.setChecked(true);
        rbn_thuThu = view.findViewById(R.id.rbt_FragThuThu);

        swipeMenuListView = view.findViewById(R.id.swlv_user);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cách 2
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentThemThanhVien()).commit();

            }
        });

        list = new ArrayList<>();
        //Lấy danh sách từ Firebase xuống Swipeview
        getAllThanhVien(getContext(), 3);
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

                        deleteThanhVienFireBase(position);
                        break;
                }

                return false;
            }
        });


        Intent intent = getActivity().getIntent();

        int quyen = intent.getIntExtra("Quyen",3);
        if(quyen == 2){
            floatingActionButton.setVisibility(View.GONE);
        }

        //Đổi danh sách xem
        clickRadio_doiDS();
    }

    private void clickRadio_doiDS(){
        rbn_thanhVien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) getAllThanhVien(getContext(), 3);

            }
        });

        rbn_thuThu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) getAllThanhVien(getContext(), 2);
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

    public void getAllThanhVien(Context context, int _quyen){
        list = new ArrayList<>();

        final CollectionReference reference = db.collection("ThanhVien");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            int MaTV = Integer.parseInt(doc.get("MaTV").toString());
                            String HoTen = doc.get("HoTen").toString();
                            String MatKhau = doc.get("MatKhau").toString();
                            String NamSinh = doc.get("NamSinh").toString();
                            String SDT = doc.get("SDT").toString();
                            String Avatar = doc.get("Avatar").toString();
                            int Quyen = Integer.parseInt(doc.get("Quyen").toString());

                            thanhVien = new ThanhVien(MaTV, HoTen, NamSinh, SDT, Avatar, MatKhau);

                            if(_quyen == 3 && Quyen == 3) list.add(thanhVien);

                            if(_quyen == 2 && Quyen == 2) list.add(thanhVien);

                        }
                        ThanhVienAdapter adapter =new ThanhVienAdapter(context, list);
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

    private void deleteThanhVienFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thông báo")
                .setMessage("Bạn chắn chắn muốn xóa thành viên không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("ThanhVien").document(list.get(positon).getMaTV()+"")
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getAllThanhVien(getContext(),3);
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

    private void updateFirebase(ThanhVien thanhVien){
        try {
            Map map = new HashMap<String, Object>();
            map.put("MaTV", thanhVien.getMaTV());
            map.put("HoTen", thanhVien.getHoTen());
            map.put("MatKhau", thanhVien.getMatKhau());
            map.put("NamSinh", thanhVien.getNamSinh());
            map.put("SDT", thanhVien.getSDT());
            map.put("Avatar", thanhVien.getAvatar());
            reference.document(thanhVien.getMaTV() + "").set(map, SetOptions.merge());

            dialog_suaThanhVien.dismiss();
            //Cập nhật lại listView
            getAllThanhVien(getContext(), 3);
        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Load hình ảnh lên ImageButton
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALEERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                contenUri = data.getData();
                String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timSamp +"."+ getFileExt(contenUri);
                imgBtn_dialogAvatarSuaTV.setImageURI(contenUri);
            }
        }
    }

    // Lấy đuôi file hình
    private  String getFileExt(Uri uri){
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("IMAGE_THANHVIEN/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Log.d("==> Done", " Load hình ảnh lên Firebase thành công "+ uri.toString());
                            // Thêm thành viên lên firebase
                            thanhVien.setAvatar(uri.toString());

                            //addThanhVienToFireStore(thanhVien);
                            updateFirebase(thanhVien);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("==> Exception", e.getMessage());
                }
            });
        }catch (Exception e){
            thanhVien.setAvatar("");
            //addThanhVienToFireStore(thanhVien);
            updateFirebase(thanhVien);
        }
    }

    private void dialog_suaThanhVien(int positon){
        dialog_suaThanhVien =  new Dialog(getContext());
        dialog_suaThanhVien.setContentView(R.layout.dialog_suathanhvien);

        dialog_suaThanhVien.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.7);
        dialog_suaThanhVien.getWindow().setLayout(width,height);

        edt_dialogTenTV = dialog_suaThanhVien.findViewById(R.id.edt_dialogTenSuaTV);
        edt_dialogNamSinhTV = dialog_suaThanhVien.findViewById(R.id.edt_dialogNamSinhSuaTV);
        edt_dialogMKTV= dialog_suaThanhVien.findViewById(R.id.edt_dialogMKSuaTV);
        btn_dialogSendSuaTV = dialog_suaThanhVien.findViewById(R.id.btn_dialogGuiSuaTV);
        imgBtn_dialogAvatarSuaTV = dialog_suaThanhVien.findViewById(R.id.imgBtn_dialogAvatarSuaTV);

        // Thêm dữ liệu vào dialog sửa
        edt_dialogTenTV.setText(list.get(positon).getHoTen());
        edt_dialogNamSinhTV.setText(list.get(positon).getNamSinh());
        edt_dialogMKTV.setText(list.get(positon).getMatKhau());

        thanhVien = list.get(positon);

        btn_dialogSendSuaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenTV = edt_dialogTenTV.getText().toString();
                String namSinhTV = edt_dialogTenTV.getText().toString();
                String matKhauTV = edt_dialogMKTV.getText().toString();

                if(tenTV.isEmpty() || namSinhTV.isEmpty() || matKhauTV.isEmpty()){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else{
                    list.get(positon).setHoTen(tenTV);
                    list.get(positon).setNamSinh(namSinhTV);
                    list.get(positon).setMatKhau(matKhauTV);

                    uploadImageToFirebase(imageFileName, contenUri);
                }


            }
        });

        // Chọn hình ảnh
        imgBtn_dialogAvatarSuaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        dialog_suaThanhVien.show();
    }
}