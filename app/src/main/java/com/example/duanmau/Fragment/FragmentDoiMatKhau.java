package com.example.duanmau.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.duanmau.LoginActivity;
import com.example.duanmau.Model.Sach;
import com.example.duanmau.Model.ThanhVien;
import com.example.duanmau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FragmentDoiMatKhau extends Fragment {

    Dialog dialogOTP;
    EditText edtMaOTP;
    Button btnSendOTP, btnSendChange;
    CheckBox cbLuumk;

    TextInputLayout  edtMk1, edtMk2;

    View view;

    private String mkNew;
    private String maTV;

    private String sdt;


    FirebaseFirestore db;
    //Image firebase
    StorageReference storageReference;

    // variable for FirebaseAuth class xác thực OTP
    private FirebaseAuth mAuth;
    // string for storing our verification ID OTP
    private String verificationId;

    public FragmentDoiMatKhau() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_doi_mat_khau, container, false);
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtMk1 = view.findViewById(R.id.tip_Pass1ChangePass);
        edtMk2 = view.findViewById(R.id.tip_Pass2ChangePass);
        btnSendChange = view.findViewById(R.id.btn_FragSubmitChangePass);
        cbLuumk = view.findViewById(R.id.cb_NhoTkChangePass);

        btnSendChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mk1 = edtMk1.getEditText().getText().toString();
                String mk2 = edtMk2.getEditText().getText().toString();

                if(!kiemLoi(mk1).isEmpty() || !kiemLoi(mk2).isEmpty()){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else if(mk1.equals(mk2)){
                    Intent intent = getActivity().getIntent();
                    mkNew = mk1;
                    sdt = intent.getStringExtra("SDT");


                    Dialog_OpenOTP(sdt);
                }else{
                    Toast.makeText(getContext(), "Mật khẩu không giống nhau, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void Dialog_OpenOTP(String sdt){
        // Gửi mã OTP đến điện thoại
        sendVerificationCode(sdt);

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

    // Gửi mã xác thực OTP
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

    //Gửi mã xác thực
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



    // Tiến hành xác thực và đăng nhập vào ứng dụng
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
                            Toast.makeText(getContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();

                            //Lấy sdt, mat khau, tk
                            Intent intent = getActivity().getIntent();
                            String maTV = intent.getStringExtra("MaTV").toString();
                            // Cập nhật mật khẩu firestore
                            updatePass(mkNew, maTV);

                            //Kiểm tra người dùng có nhấn checkbox nhớ tài khoản ko
                            remeberUser(sdt, mkNew, cbLuumk.isChecked());

                            //Chuyển sang fragment Trang chính
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_TrangChinh_fragment, new FragmentTrangChinh()).commit();

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

    //Thay đổi password
    private void updatePass(String mk, String MaTV){
        try {
            db.collection("ThanhVien").document(MaTV).update("MatKhau", mk);
        }catch (Exception e){
            Toast.makeText(getContext(), "Không đổi được mật khẩu, lỗi "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Check box lưu mk
    public void remeberUser(String u, String p, boolean status){
        SharedPreferences pref =getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(!status){
            edit.clear();
        }else{
            //Luu du lieu
            edit.putString("USERNAME", u);
            edit.putString("PASSWORD", p);
            edit.putBoolean("STATUS", status);
        }

        //luu lai toan bo
        edit.commit();
    }

    //Kiểm lỗi mật khẩu
    private String kiemLoi(String mk){
        String error="";
        Boolean hoa = true, thuong = true, so = true;
        if(mk.length()<8) error = "Mật khẩu quá ngắn";

        for(int i=0; i<mk.length();i++){
            int ascii = (int) mk.charAt(0);
            if(ascii > 64 && ascii < 91) hoa = false;
            if(ascii > 96 && ascii < 123) thuong = false;
            if(ascii > 47 && ascii < 58) so = false;
        }

        if(hoa) error += " thiếu kí tự hoa";
        if(thuong) error += " thiếu kí tự hoa";
        if(so) error += " thiếu kí tự hoa";

        return error;
    }

}