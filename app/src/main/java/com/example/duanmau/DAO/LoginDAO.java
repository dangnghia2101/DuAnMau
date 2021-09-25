package com.example.duanmau.DAO;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.duanmau.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginDAO {
    private FirebaseFirestore firestore;
    String kq ="";

    public LoginDAO(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.kq = "";
    }

    public void CheckAccount(Context context, String userName, String pass){

        firestore =  FirebaseFirestore.getInstance();
        final CollectionReference reference = firestore.collection("ThanhVien");

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    for(QueryDocumentSnapshot doc: snapshot){
                        if(userName.equals(doc.get("SDT")) && pass.equals(doc.get("MatKhau"))){
                            context.startActivity(new Intent(context, MainActivity.class));
                        }else{
                            Toast.makeText(context, "Tài khoản hoặc mật khẩu không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


    }
}
