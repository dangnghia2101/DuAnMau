package com.example.duanmau.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.duanmau.Adapter.LoaiSachAdapter;
import com.example.duanmau.Model.LoaiSach;
import com.example.duanmau.Model.Sach;
import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentThemSach extends Fragment {
    EditText edt_Masach, edtTenSach, edt_SoLuong, edt_giasach;
    Spinner sp_MaLoai;
    Button btnAddSach;
    ImageButton imgBtnExit, imgBtnAvatar;

    List<Sach> list;

    private int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    Sach sach;

    //Danh sách cho spinner
    List<String> listMaLoai;
    private String maLoaiSach;

    //Firebase
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    public FragmentThemSach() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentThemSach newInstance(String param1, String param2) {
        FragmentThemSach fragment = new FragmentThemSach();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_them_sach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhxa(view);

        //Spinner mã loại
        getMaLoaiSachToSpiner();
        sp_MaLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maLoaiSach = listMaLoai.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Thoát màn hình thêm thành viên
        imgBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentSach()).commit();
            }
        });

        // Thêm ảnh đại diện
        imgBtnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        // Cập nhật thêm thành viên
        btnAddSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int maSach = Integer.parseInt(edt_Masach.getText().toString());
                    int giaThue = Integer.parseInt(edt_giasach.getText().toString());
                    int soLuong = Integer.parseInt(edt_SoLuong.getText().toString());
                    String maLoai = maLoaiSach;
                    String tenSach = edtTenSach.getText().toString();


                    if( tenSach.isEmpty()){
                        Toast.makeText(getContext(), "Không được để trống ô nhập", Toast.LENGTH_SHORT).show();
                    }else{
                        //Load hình ảnh lên firebase
                        if(imageFileName.isEmpty()){
                            Toast.makeText(getContext(), "Chưa thêm hình ảnh", Toast.LENGTH_SHORT).show();
                        }else {
                            //THêm thành viên vô database
                            sach = new Sach(maSach, maLoai, tenSach, soLuong, giaThue,"");


//                           Kiểm tra tài mã thành viên đã tồn tại chưa
                            final CollectionReference reference = db.collection("Sach");
                            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int check = 0;
                                    if(task.isSuccessful()){
                                        QuerySnapshot snapshot = task.getResult();
                                        for(QueryDocumentSnapshot doc: snapshot){
                                            if(String.valueOf(sach.getMaSach()).equals(doc.get("MaSach").toString())){
                                                check = 1;
                                                break;
                                            }
                                        }
                                        if(check == 0){
                                            uploadImageToFirebase(imageFileName, contenUri);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentSach()).commit();
                                        }else Toast.makeText(getContext(), "Mã thành viên đã tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Không được để trống ô nhập. Lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void anhxa(View view){
        edt_Masach = view.findViewById(R.id.edt_fragMaSachThemSach);
        edtTenSach = view.findViewById(R.id.edt_fragTenSachThemSach);
        edt_SoLuong = view.findViewById(R.id.edt_fragSoLuongThemSach);
        edt_giasach = view.findViewById(R.id.edt_fragGiaThemSach);
        sp_MaLoai = view.findViewById(R.id.sp_fragLoaiSachThemSach);
        imgBtnExit = view.findViewById(R.id.imgBtn_fragExitThemSach);
        imgBtnAvatar = view.findViewById(R.id.imgBtn_fragAddAvatarSach);
        btnAddSach = view.findViewById(R.id.btn_fragAddSach);

    }


    private void addSachToFireStore(Sach sach){
        //creating collection reference
        //for our Firebase FireStore database

        final CollectionReference collectionReference = db.collection("Sach");

        Map<String, Object> data = new HashMap<>();
        data.put("Avatar", sach.getAvatar());
        data.put("TenSach", sach.getTenSach());
        data.put("MaSach", sach.getMaSach());
        data.put("MaLoai", sach.getMaLoai());
        data.put("SoLuong", sach.getSoLuong());
        data.put("GiaThue", sach.getGiaThue());

        try {
            collectionReference.document(sach.getMaSach() + "").set(data);

        }catch (Exception e){
            Log.d("Error_addTVFirebase", e.getMessage());
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
                imgBtnAvatar.setImageURI(contenUri);
            }
        }
    }


    private  String getFileExt(Uri uri){
        ContentResolver c = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

    private void uploadImageToFirebase(String name, Uri contentUri){
        StorageReference image = storageReference.child("IMAGE_SACH/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Thêm sach lên firebase
                            sach.setAvatar(uri.toString());
                            addSachToFireStore(sach);
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
            sach.setAvatar("");
            addSachToFireStore(sach);
        }
    }

    private void getMaLoaiSachToSpiner(){

        listMaLoai = new ArrayList<>();

        final CollectionReference reference = db.collection("LoaiSach");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if(task.isSuccessful()){
                        QuerySnapshot snapshot = task.getResult();
                        for(QueryDocumentSnapshot doc: snapshot){
                            String MaLoai = doc.get("MaLoai").toString();

                            listMaLoai.add(MaLoai);

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, listMaLoai);

                        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        sp_MaLoai.setAdapter(adapter);
                    }else{
                        Toast.makeText(getContext(), "Kiểm tra kết nối mạng của bạn. Lỗi "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Lấy giá trị từ spinner xuống

    //Spinner



}