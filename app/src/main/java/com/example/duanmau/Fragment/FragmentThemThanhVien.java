package com.example.duanmau.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class FragmentThemThanhVien extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton rbt_thuThu, rbt_thanhVien;
    private int Quyen=3;

    EditText edt_maTV, edtTenTV, edtNamSinh, edtSDT, edtMaOTP;
    ImageButton imgBtnAvatar, imgBtnExit;
    Button btnAddThanhVien, btnSendOTP;

    // variable for FirebaseAuth class x??c th???c OTP
    private FirebaseAuth mAuth;
    // string for storing our verification ID OTP
    private String verificationId;

    //CSDL
    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    Dialog dialogOTP;

    //Load image
    int GALEERY_REQUEST_CODE = 105;
    Uri contenUri;
    String imageFileName ="";

    ThanhVien thanhVien;

    View view;

    public FragmentThemThanhVien() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_them_thanh_vien, container, false);

        anhxa();

        // Tho??t m??n h??nh th??m th??nh vi??n
        imgBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentThanhVien()).commit();
            }
        });

        // Th??m ???nh ?????i di???n
        imgBtnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALEERY_REQUEST_CODE);
            }
        });

        // C???p nh???t th??m th??nh vi??n
        btnAddThanhVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int maTV = Integer.parseInt(edt_maTV.getText().toString());
                    String tenTV = edtTenTV.getText().toString();
                    String SDT = edtSDT.getText().toString();
                    String namSinh = edtNamSinh.getText().toString();

                    if( tenTV.isEmpty() || SDT.isEmpty() || namSinh.isEmpty()){
                        Toast.makeText(getContext(), "Kh??ng ???????c ????? tr???ng ?? nh???p", Toast.LENGTH_SHORT).show();
                    }else{
                        //Load h??nh ???nh l??n firebase
                        if(imageFileName.isEmpty()){
                            Toast.makeText(getContext(), "Ch??a th??m h??nh ???nh", Toast.LENGTH_SHORT).show();
                        }else {
                            //TH??m th??nh vi??n v?? database
                            thanhVien = new ThanhVien(maTV, tenTV, namSinh, SDT, "", SDT);

                            db = FirebaseFirestore.getInstance();

//                           Ki???m tra t??i m?? th??nh vi??n ???? t???n t???i ch??a
                            final CollectionReference reference = db.collection("ThanhVien");
                            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int check = 0;
                                    if(task.isSuccessful()){
                                        QuerySnapshot snapshot = task.getResult();
                                        for(QueryDocumentSnapshot doc: snapshot){
                                            //Log.d("=======> ", doc.get("MaTV").toString());
                                            if(String.valueOf(thanhVien.getMaTV()).equals(doc.get("MaTV").toString()) || thanhVien.getSDT().equals(doc.get("SDT").toString())){
                                                check = 1;
                                                break;
                                            }
                                        }
                                        if(check == 0){
                                            //M??? dialog nh???p OTP
                                            Dialog_OpenOTP();
                                        }else Toast.makeText(getContext(), "M?? th??nh vi??n ???? t???n t???i", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "L???i " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Checked radio button
        checked_radioBtn();

        return view;
    }

    private void anhxa(){
        // ??nh x???
        edt_maTV = (EditText) view.findViewById(R.id.edt_dialogAddMaThanhVien);
        edtTenTV = (EditText) view.findViewById(R.id.edt_dialogAddTenThanhVien);
        edtSDT = (EditText) view.findViewById(R.id.edt_dialogAddSDTThanhVien);
        edtNamSinh = (EditText) view.findViewById(R.id.edt_dialogAddNamSinhThanhVien);
        imgBtnAvatar = (ImageButton) view.findViewById(R.id.imgBtn_dialogAddAvatarThanhVien);
        imgBtnExit = (ImageButton) view.findViewById(R.id.imgBtn_fragExitThemThanhVien);
        btnAddThanhVien = (Button) view.findViewById(R.id.btn_dialogAddThanhVien);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupThemTV);
        rbt_thuThu = (RadioButton) view.findViewById(R.id.rbt_thuThu);
        rbt_thanhVien = (RadioButton) view.findViewById(R.id.rbt_thanhVien);
        rbt_thanhVien.setChecked(true);
    }

    private void checked_radioBtn(){
        rbt_thanhVien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Quyen = 3;
            }
        });

        rbt_thuThu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Quyen = 2;
            }
        });
    }


    private void addThanhVienToFireStore(ThanhVien thanhVien){
        //creating collection reference
        //for our Firebase FireStore database

        final CollectionReference collectionReference = db.collection("ThanhVien");

        Map<String, Object> data = new HashMap<>();
        data.put("Avatar", thanhVien.getAvatar());
        data.put("HoTen", thanhVien.getHoTen());
        data.put("MaTV", thanhVien.getMaTV());
        data.put("MatKhau", thanhVien.getMatKhau());
        data.put("NamSinh", thanhVien.getNamSinh());
        data.put("SDT", thanhVien.getSDT());
        data.put("Quyen", Quyen);


        try {
            collectionReference.document(thanhVien.getMaTV() + "").set(data);
        }catch (Exception e){
            Log.d("Error_addTVFirebase", e.getMessage());
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
        StorageReference image = storageReference.child("IMAGE_THANHVIEN/"+name);
        try {
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Log.d("==> Done", " Load h??nh ???nh l??n Firebase th??nh c??ng "+ uri.toString());
                            // Th??m th??nh vi??n l??n firebase
                            thanhVien.setAvatar(uri.toString());
                            addThanhVienToFireStore(thanhVien);
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
            addThanhVienToFireStore(thanhVien);
        }
    }

    private void Dialog_OpenOTP(){
        // G???i m?? OTP ?????n ??i???n tho???i
        sendVerificationCode(edtSDT.getText().toString());

        dialogOTP = new Dialog(getContext());
        dialogOTP.setContentView(R.layout.dialog_otp_firebase);

        dialogOTP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.6);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        dialogOTP.getWindow().setLayout(width,height);

        edtMaOTP = dialogOTP.findViewById(R.id.edt_DialogNhapOTP);
        btnSendOTP = dialogOTP.findViewById(R.id.btn_DialogGuiOTP);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(edtMaOTP.getText().toString());
            }
        });

        dialogOTP.show();

    }

    // G???i m?? x??c th???c OTP
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                edtMaOTP.setText(code);
                //verifying the code
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), "PhoneAuthProvider "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            //ResendToken = forceResendingToken;
        }
    };

    //G???i m?? x??c th???c
    private void sendVerificationCode(String mobile) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+84"+mobile)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(getActivity())
                        .setCallbacks(mCallback)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    // Ti???n h??nh x??c th???c v?? ????ng nh???p v??o ???ng d???ng
    private void verifyCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        //signing the user
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "X??c th???c th??nh c??ng", Toast.LENGTH_SHORT).show();
                            // Load avatar l??n firebase
                            uploadImageToFirebase(imageFileName, contenUri);

                            //Chuy???n sang fragment Thanh Vien
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentThanhVien()).commit();

                            dialogOTP.dismiss();
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(view.findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }



}