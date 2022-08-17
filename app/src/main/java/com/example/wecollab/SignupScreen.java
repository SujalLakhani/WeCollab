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
import java.util.regex.Pattern;

public class SignupScreen extends AppCompatActivity {
    public Button SignUp;
    public EditText Username;
    public EditText PhoneNo;
    public EditText Password;
    public TextView old;
    public FirebaseFirestore db;
    public ProgressBar pb;
    public static String pn;
    public static String pw;
    public boolean check;
    public long backPressedTime;
    public Toast backToast;

    public int create() {
        if(Username.getText().length()<=2 || !(Pattern.matches("[a-zA-Z]+",Username.getText().toString()))){
            Username.setError("Enter valid Username!!");
            Username.requestFocus();
            return 0;
        }

        if(!(PhoneNo.getText().length()>0)){
            PhoneNo.setError("Enter Your Phone Number!!");
            PhoneNo.requestFocus();
            return 0;
        }

        if(!checkpn(pn))
            return 0;

        pw = Password.getText().toString();
        if(!(Password.getText().length()>5))
        {
            Password.setError("Password must have atleast 6 charecters!!");
            Password.requestFocus();
            return 0;
        }

        return 1;

    }
    public boolean checkpn(String pn)
    {
        try
        {

            Long.parseLong(pn);

            if (pn.length() != 10){
                PhoneNo.setError("Enter Valid Phone Number!!");
                PhoneNo.requestFocus();
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

    private void userLogin()
    {

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d:list){
                        String s=d.getString("Phone");
                        if(pn.equals(s)){
                            check=false;
                            PhoneNo.setError("This Phone Number is already registered!!");
                            pb.setVisibility(View.GONE);
                            PhoneNo.requestFocus();
                        }
                    }
                    if(check) {

                        verification();
                    }
                    else{
                        pb.setVisibility(View.GONE);
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupScreen.this,"Failed to find document!!",Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });

    }

    public void verification()
    {
        LoginScreen.id=pn;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+pn,
                60,
                TimeUnit.SECONDS,
                SignupScreen.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(SignupScreen.this, "Your Phone No is Verified Successfully!!", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SignupScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Intent i = new Intent(SignupScreen.this, OTPScreen.class);
                        i.putExtra("OTP", s);
                        startActivity(i);
                        finish();
                        pb.setVisibility(View.GONE);
                    }
                }

        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        SignUp=findViewById(R.id.SignUp);
        PhoneNo=findViewById(R.id.PhoneNo);
        old=findViewById(R.id.old);
        Username=findViewById(R.id.Username);
        Password=findViewById(R.id.Password);
        db = FirebaseFirestore.getInstance();
        pb=findViewById(R.id.progressBar2);



        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                pn = PhoneNo.getText().toString();
                pw = Password.getText().toString();
                if(create()!=0) {
                    check = true;
                    userLogin();
                }
                else
                {
                    pb.setVisibility(View.GONE);
                }
            }
        });
        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignupScreen.this, LoginScreen.class);
                startActivity(i);
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