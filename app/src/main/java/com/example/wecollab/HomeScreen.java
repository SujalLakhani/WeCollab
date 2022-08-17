package com.example.wecollab;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class HomeScreen extends AppCompatActivity {
    public static TextView textView;
    public static EditText passcode;
    public static Button join,create;
    public long backPressedTime;
    public Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        textView=findViewById(R.id.textView3);
        passcode=findViewById(R.id.passcode);
        join=findViewById(R.id.join);
        create=findViewById(R.id.create);
        URL serverurl;

        try{
            serverurl=new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions=new JitsiMeetConferenceOptions.Builder().setServerURL(serverurl).setWelcomePageEnabled(false).build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }

        new AlertDialog.Builder(HomeScreen.this).setIcon(R.drawable.ic_check).setTitle("Success!!").setMessage("You have Successfully Log in!!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder().setRoom(passcode.getText().toString()).setWelcomePageEnabled(false).build();
                JitsiMeetActivity.launch(HomeScreen.this,options);

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder().setRoom(LoginScreen.id).setWelcomePageEnabled(false).build();
                JitsiMeetActivity.launch(HomeScreen.this,options);

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