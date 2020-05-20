package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity{
    public static final String TAG = "TAG";
    Button Profile_done;
    Spinner spinnerSemester;
    Spinner spinnerStream;
    private EditText editTextRollno;
    private EditText editTextName;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth = FirebaseAuth.getInstance();
//        if (firebaseAuth.getCurrentUser() != null) {
//            finish();
//            startActivity(new Intent(setProfile.this, DashBoard.class));
//        }
        userID = firebaseAuth.getCurrentUser().getUid();
        editTextName =  findViewById(R.id.user_name);
        editTextRollno = findViewById(R.id.roll_no);
        spinnerStream = findViewById(R.id.stream);
        spinnerSemester = findViewById(R.id.semester);
        Profile_done =  findViewById(R.id.profile_done);
        fstore = FirebaseFirestore.getInstance();
        Profile_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name, Rollno,Stream,Semester;
                Name = editTextName.getText().toString();
                Rollno = editTextRollno.getText().toString();
                Stream = spinnerStream.getSelectedItem().toString();
                Semester = spinnerSemester.getSelectedItem().toString();
                if (TextUtils.isEmpty(Name)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Name....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Rollno)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Roll No....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Stream)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Straem....", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Semester)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Semester....", Toast.LENGTH_SHORT).show();
                    return;
                }
                userInformation();
                finish();
                startActivity(new Intent(setProfile.this, DashBoard.class));
            }
        });
    }
    private void userInformation() {
        String name = editTextName.getText().toString().trim();
        String rollno = editTextRollno.getText().toString().trim();
        String semester = spinnerSemester.getSelectedItem().toString().trim();
        String stream  = spinnerStream.getSelectedItem().toString().trim();
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("Users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("Name",name);
        user.put("Roll_no",rollno);
        user.put("Semester",semester);
        user.put("Stream",stream);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"onSuccess: user profile is created");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure: "+ e.toString());
            }
        });
        Toast.makeText(setProfile.this, "User Information updated", Toast.LENGTH_SHORT).show();
    }

}
