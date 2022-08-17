package com.example.wecollab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginScreen extends AppCompatActivity {
    public Button LogIn,newac;
    public EditText PhoneNo,Password;
    public TextView fp;
    public ProgressBar pb;
    public static FirebaseFirestore db;
    public static String pn;
    public String pw;
    public boolean check;
    public static String id;
    public static boolean forgotPasswoerd=false;
    public long backPressedTime;
    public Toast backToast;

    public boolean checkpn(String pn, String pw)
    {
        try
        {
            if(pn.length()<=0)
            {
                PhoneNo.setError("Enter Your Phone Number!!");
                PhoneNo.requestFocus();
                pb.setVisibility(View.GONE);
                return false;
            }
            if (pn.length() != 10){
                PhoneNo.setError("Enter Valid Phone Number!!");
                PhoneNo.requestFocus();
                pb.setVisibility(View.GONE);
                return false;
            }
            Long.parseLong(pn);
            if(pw.length()<=0){
                Password.setError("Enter your Password!!");
                Password.requestFocus();
                pb.setVisibility(View.GONE);
                return false;
            }
        }
        catch (Exception e)
        {
            PhoneNo.setError("Enter Valid Phone Number!!");
            PhoneNo.requestFocus();
            pb.setVisibility(View.GONE);
            return false;
        }
        return true;
    }
    public void verification()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+pn,
                1,
                TimeUnit.SECONDS,
                LoginScreen.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(LoginScreen.this, "Your Phone No is Verified Successfully!!", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Intent i = new Intent(LoginScreen.this, OTPScreen.class);
                        i.putExtra("OTP", s);
                        startActivity(i);
                        finish();
                        pb.setVisibility(View.GONE);
                    }
                }

        );
    }

    private void userLogin()
    {
        pb.setVisibility(View.VISIBLE);
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d:list){
                        if(pn.equals(d.getString("Phone"))){
                            check=false;
                            id=pn;
                            if(forgotPasswoerd){
                                verification();
                            }
                            else
                            {
                                if(pw.equals(d.getString("Password"))){
                                    pb.setVisibility(View.GONE);
                                    Intent i=new Intent(LoginScreen.this, HomeScreen.class);
                                    startActivity(i);

                                }
                                else{
                                    Password.setError("You have entered wrong Password!!");
                                    Password.requestFocus();
                                    pb.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    if(check){
                        PhoneNo.setError("Phone Number is not registered!!");
                        PhoneNo.requestFocus();
                        pb.setVisibility(View.GONE);

                    }
                }
                else{
                    Toast.makeText(LoginScreen.this,"Sorry Phone Number is not found!!",Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginScreen.this,"Failed to find document!!",Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        LogIn=findViewById(R.id.LogIn);
        PhoneNo=findViewById(R.id.PhoneNo);
        newac=findViewById(R.id.newac);
        Password=findViewById(R.id.Password);
        db = FirebaseFirestore.getInstance();
        pb=findViewById(R.id.progressBar);
        fp=findViewById(R.id.fp);
        newac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(LoginScreen.this, SignupScreen.class);
                startActivity(i);
            }
        });
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                pn=PhoneNo.getText().toString();
                pw=Password.getText().toString();
                if(checkpn(pn,pw)) {
                    check = true;
                    userLogin();

                }

            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                pn=PhoneNo.getText().toString();
                pw="1234";
                if(checkpn(pn,pw)){
                    forgotPasswoerd=true;
                    userLogin();
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