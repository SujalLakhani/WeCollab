package com.example.wecollab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class NewPasswordScreen extends AppCompatActivity {
    private FirebaseFirestore db;
    public Button changePassword;
    public EditText newPassword,conformPassword;
    public ProgressBar pb;
    public long backPressedTime;
    public Toast backToast;

    public void updatePassword(String np){
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d:list) {
                        if (LoginScreen.pn.equals(d.getString("Phone"))) {
                            HashMap hashMap=new HashMap();
                            hashMap.put("Password",np);
                            db.collection("users").document(d.getId()).update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    LoginScreen.forgotPasswoerd=false;
                                    Toast.makeText(NewPasswordScreen.this,"Your Password is updated Successfully!!",Toast.LENGTH_SHORT).show();
                                    pb.setVisibility(View.GONE);
                                    startActivity(new Intent(NewPasswordScreen.this, LoginScreen.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewPasswordScreen.this,"Sorry, your Password is Not updated!!",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(NewPasswordScreen.this,"Please try again!!",Toast.LENGTH_SHORT).show();
                                    pb.setVisibility(View.GONE);

                                }
                            });
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewPasswordScreen.this,"Sorry, there is some issue!!",Toast.LENGTH_SHORT).show();
                Toast.makeText(NewPasswordScreen.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                pb.setVisibility(View.GONE);
            }
        });
    }

    public boolean checkPassword(String np,String cp){
        if(np.isEmpty())
        {
            newPassword.setError("Enter Your New Password here!!");
            newPassword.requestFocus();
            pb.setVisibility(View.GONE);
            return false;
        }
        if(cp.isEmpty())
        {
            conformPassword.setError("Conform Your New Password!!");
            conformPassword.requestFocus();
            pb.setVisibility(View.GONE);
            return false;
        }
        if(np.length()<6){
            newPassword.setError("Password must have at least 6 charecters!!");
            newPassword.requestFocus();
            pb.setVisibility(View.GONE);
            return false;
        }
        if (!np.equals(cp)){
            conformPassword.setError("This Password is Not matched with your New Password!!");
            conformPassword.requestFocus();
            pb.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password_screen);

        db = LoginScreen.db;
        changePassword=findViewById(R.id.chagePassword);
        newPassword=findViewById(R.id.newPassword);
        conformPassword=findViewById(R.id.conformPassword);
        pb=findViewById(R.id.progressBar4);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if(checkPassword(newPassword.getText().toString(),conformPassword.getText().toString())){
                    updatePassword(newPassword.getText().toString());
                }
            }
        });

    }
    @Override
    public void onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()){
            backToast.cancel();
            finishAffinity();
            System.exit(0);
        }
        else {
            backToast = Toast.makeText(getBaseContext(),"Press Back again to Exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime=System.currentTimeMillis();

    }
}