package com.example.duanmau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duanmau.DAO.LoginDAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.logging.Handler;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tip_username, tip_pass;
    Button btnSubmit;
    CheckBox cb_luuTk;
    TextView txtQuyenTK;

    LoginDAO loginDAO;
    public FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        anhxa();

        SharedPreferences pref =getSharedPreferences("USER_FILE", MODE_PRIVATE);
        tip_username.getEditText().setText(pref.getString("USERNAME",""));
        tip_pass.getEditText().setText(pref.getString("PASSWORD",""));
        cb_luuTk.setChecked(pref.getBoolean("STATUS", false));

        //loginDAO = new LoginDAO(firestore);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tip_username.getEditText().getText().toString();
                String pass = tip_pass.getEditText().getText().toString();

                if(username.isEmpty() || pass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                }else {
                    CheckAccount(username, pass);
                }
            }
        });
    }

    private void anhxa(){
        tip_username = findViewById(R.id.tip_UserLogin);
        tip_pass = findViewById(R.id.tip_PassLogin);
        btnSubmit = findViewById(R.id.btn_SubmitLogin);
        cb_luuTk = findViewById(R.id.cb_NhoTk);
        txtQuyenTK = findViewById(R.id.txtQuyenTK);
    }

    public void CheckAccount(String userName, String pass){

        firestore =  FirebaseFirestore.getInstance();
        final CollectionReference reference = firestore.collection("ThanhVien");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int check = 0;
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    for(QueryDocumentSnapshot doc: snapshot){
                        if(userName.equals(doc.get("SDT")) && pass.equals(doc.get("MatKhau"))){
                            //
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("SDT", userName);
                            intent.putExtra("HoTen", doc.get("HoTen").toString());
                            intent.putExtra("Avatar", doc.get("Avatar").toString());

                            startActivity(intent);
                            remeberUser(userName, pass, cb_luuTk.isChecked());
                            check = 1;
                            break;
                        }
                    }
                    if(check == 0){
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void remeberUser(String u, String p, boolean status){
        SharedPreferences pref =getSharedPreferences("USER_FILE", MODE_PRIVATE);
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

}