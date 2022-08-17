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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OTPScreen extends AppCompatActivity {
    public Button verify;
    public EditText OTP;
    public String s;
    public ProgressBar pb;
    public FirebaseFirestore db;
    public long backPressedTime;
    public Toast backToast;

    private void userSignup()
    {

        Map<String, Object> user = new HashMap<>();
        user.put("Phone", SignupScreen.pn);
        user.put("Password", SignupScreen.pw);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(OTPScreen.this, "Data is saved successfully!!", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(OTPScreen.this, HomeScreen.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OTPScreen.this, "Failed to save data!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);
        verify=findViewById(R.id.button);
        OTP=findViewById(R.id.editTextTextPersonName2);
        s=getIntent().getStringExtra("OTP");
        pb=findViewById(R.id.progressBar3);
        db=FirebaseFirestore.getInstance();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                String OTPv=OTP.getText().toString();
                if(OTPv.isEmpty()){
                    OTP.setError("Enter Your OTP here!!");
                    OTP.requestFocus();
                }
                else {
                    if(s.isEmpty())
                    {
                        Toast.makeText(OTPScreen.this,"Check your Internet connection!1",Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                    else{
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(s,OTPv);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    if (LoginScreen.forgotPasswoerd) {
                                        startActivity(new Intent(OTPScreen.this, NewPasswordScreen.class));
                                        finish();
                                    }
                                    else{ userSignup(); }


                                }
                                else{
                                    OTP.setError("You have entered wrong OTP!! ");
                                    OTP.requestFocus();
                                    pb.setVisibility(View.GONE);
                                }

                            }
                        });

                    }

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