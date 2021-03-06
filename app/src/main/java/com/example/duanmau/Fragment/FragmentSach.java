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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.duanmau.Adapter.SachAdapter;
import com.example.duanmau.Adapter.ThanhVienAdapter;
import com.example.duanmau.Model.Sach;
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

public class FragmentSach extends Fragment {

    private SwipeMenuListView swipeMenuListView;
    public List<Sach> list;
    FloatingActionButton floatingActionButton;

    EditText edt_dialogTenSach, edt_dialogSLSach, edt_dialogGiaThueSach;
    ImageButton imgBtn_dialogAvatarSuaSach;
    Button btn_dialogGuiSuaSach;


    //Firestore
    FirebaseFirestore db;
    final CollectionReference reference = FirebaseFirestore.getInstance().collection("Sach");


    Sach sach;

    private int Quyen = 3;


    Dialog dialog_suaSach;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    //Image firebase
    StorageReference storageReference;


    public FragmentSach() {
        // Required empty public constructor
    }

    public static FragmentSach newInstance(String param1, String param2) {
        FragmentSach fragment = new FragmentSach();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        getAllSach(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingActionButton = view.findViewById(R.id.flbtn_addSach);
        swipeMenuListView = view.findViewById(R.id.swlv_sach);

        list = new ArrayList<>();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentThemSach()).commit();

            }
        });

        //L???y danh s??ch s??ch t??? firebase xu???ng

        //Event clock swipemenu
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        dialog_suaSach(position);
                        break;
                    case 1:
                        deleteSachFireBase(position);
                        break;
                }
                return false;
            }
        });

        //L???y quy???n t??i kho???n
        Intent intent = getActivity().getIntent();
        Quyen = intent.getIntExtra("Quyen", 3);

        hide_quyen();
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

    public void getAllSach(Context context){
        list = new ArrayList<>();

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

                            sach = new Sach(MaSach, MaLoai, TenSach, SoLuong, GiaThue, Avatar);
                            list.add(sach);

                        }
                        SachAdapter adapter =new SachAdapter(context, list);
                        swipeMenuListView.setAdapter(adapter);
                    }else{
                        Toast.makeText(getContext(), "Ki???m tra k???t n???i m???ng c???a b???n. L???i "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteSachFireBase(int positon){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Th??ng b??o")
                .setMessage("B???n ch???n ch???n mu???n x??a s??ch kh??ng?")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("Sach").document(list.get(positon).getMaSach()+"")
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getAllSach(getContext());
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

    private void updateFirebase(Sach sach){
        try {
            Map map = new HashMap<String, Object>();
            map.put("MaSach", sach.getMaSach());
            map.put("MaLoai", sach.getMaLoai());
            map.put("TenSach", sach.getTenSach());
            map.put("SoLuong", sach.getSoLuong());
            map.put("GiaThue", sach.getGiaThue());
            map.put("Avatar", sach.getAvatar());
            reference.document(sach.getMaSach() + "").set(map, SetOptions.merge());

            dialog_suaSach.dismiss();
            //C???p nh???t l???i listView
            getAllSach(getContext());
        }catch (Exception e){
            Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Load h??nh ???nh l??n ImageButton
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALEERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                contenUri = data.getData();
                String timSamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timSamp +"."+ getFileExt(contenUri);
                imgBtn_dialogAvatarSuaSach.setImageURI(contenUri);
            }
        }
    }

    // L???y ??u??i file h??nh
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
                            // Th??m s??ch l??n firebase
                            sach.setAvatar(uri.toString());

                            updateFirebase(sach);
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
            updateFirebase(sach);
            Toast.makeText(getContext(), "Ch??a c?? h??nh, l???i "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void dialog_suaSach(int positon){
        dialog_suaSach =  new Dialog(getContext());
        dialog_suaSach.setContentView(R.layout.dialog_suasach);

        dialog_suaSach.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.7);
        dialog_suaSach.getWindow().setLayout(width,height);


        edt_dialogTenSach = dialog_suaSach.findViewById(R.id.edt_dialogTenSuaSach);
        edt_dialogGiaThueSach= dialog_suaSach.findViewById(R.id.edt_dialogGiaThueSuaSach);
        edt_dialogSLSach= dialog_suaSach.findViewById(R.id.edt_dialogSoLuongSuaSach);
        btn_dialogGuiSuaSach = dialog_suaSach.findViewById(R.id.btn_dialogGuiSuaSach);
        imgBtn_dialogAvatarSuaSach = dialog_suaSach.findViewById(R.id.imgBtn_dialogAvatarSuaSach);

        // Th??m d??? li???u v??o dialog s???a
        edt_dialogTenSach.setText(list.get(positon).getTenSach());
        edt_dialogSLSach.setText(list.get(positon).getSoLuong()+"");
        edt_dialogGiaThueSach.setText(list.get(positon).getGiaThue()+"");

        sach = list.get(positon);

        btn_dialogGuiSuaSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSach = edt_dialogTenSach.getText().toString();
                String soluongSach = edt_dialogSLSach.getText().toString();
                String giaSach = edt_dialogGiaThueSach.getText().toString();

                if(tenSach.isEmpty() || soluongSach.isEmpty() ||  giaSach.isEmpty()){
                    Toast.makeText(getContext(), "Kh??ng ???????c ????? tr???ng", Toast.LENGTH_SHORT).show();
                }else{
                    list.get(positon).setTenSach(tenSach);
                    list.get(positon).setSoLuong(Integer.parseInt(soluongSach));
                    list.get(positon).setGiaThue(Integer.parseInt(giaSach));

                    if(imageFileName.isEmpty()){
                        updateFirebase(list.get(positon));
                    }else uploadImageToFirebase(imageFileName, contenUri);
                }


            }
        });

        // Ch???n h??nh ???nh
        imgBtn_dialogAvatarSuaSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        dialog_suaSach.show();
    }

    //???n t??c v??? cho t???ng ng?????i d??ng
    private void hide_quyen(){
        if(Quyen == 3) {
            floatingActionButton.setVisibility(View.GONE);
        }else{
            createSwipeMenu();
        }
    }

}