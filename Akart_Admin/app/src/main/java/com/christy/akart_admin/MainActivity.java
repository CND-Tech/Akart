package com.christy.akart_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private Button scan;
    private String qrText;
    private FirebaseFirestore firebaseFirestore;
    private TextView qty_journel, qty_hardbounds, qty_graphs, qty_index, cost_journel, cost_hardbounds, cost_graphs, cost_index, total, name, semester, roll_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qrScan = new IntentIntegrator(this);
        scan = findViewById(R.id.btn_scan);
        firebaseFirestore = FirebaseFirestore.getInstance();

        qty_journel = findViewById(R.id.journel_qty);
        qty_hardbounds = findViewById(R.id.hardbound_qty);
        qty_graphs = findViewById(R.id.graph_qty);
        qty_index = findViewById(R.id.index_qty);
        cost_journel = findViewById(R.id.cost_journel);
        cost_hardbounds = findViewById(R.id.cost_hardbound);
        cost_graphs = findViewById(R.id.cost_graphs);
        cost_index = findViewById(R.id.cost_index);
        total = findViewById(R.id.total);
        name = findViewById(R.id.name);
        roll_no = findViewById(R.id.roll_no);
        semester = findViewById(R.id.semester);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null ){
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
            } else {
                qrText = result.getContents().trim();
                DocumentReference documentReference = firebaseFirestore.collection("Items").document(qrText);
                DocumentReference doc = firebaseFirestore.collection("Users").document(qrText);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                Map<String, Object> map = documentSnapshot.getData();
                                String Journel = (String) map.get("Journels");
                                String Hard_Bounds = (String) map.get("Hard_Bounds");
                                String Graphs = (String) map.get("Graphs");
                                String Index = (String) map.get("Index");
                                if (Journel.isEmpty()){
                                    Journel = "0";
                                }
                                if (Hard_Bounds.isEmpty()){
                                    Hard_Bounds = "0";
                                }
                                if (Graphs.isEmpty()){
                                    Graphs = "0";
                                }
                                if (Index.isEmpty()){
                                    Index = "0";
                                }
                                qty_journel.setText(Journel);
                                qty_hardbounds.setText(Hard_Bounds);
                                qty_graphs.setText(Graphs);
                                qty_index.setText(Index);
                                int cst_journel, cst_hardbounds, cst_graph, cst_index, total_cost;
                                cst_journel = Integer.parseInt(Journel.trim()) * 25;
                                cst_hardbounds = Integer.parseInt(Hard_Bounds.trim()) * 12;
                                cst_graph = Integer.parseInt(Graphs.trim()) * 3;
                                cst_index = Integer.parseInt(Index.trim()) * 3;
                                total_cost = cst_graph + cst_hardbounds + cst_index + cst_journel;

                                cost_journel.setText(cst_journel+"Rs");
                                cost_hardbounds.setText(cst_hardbounds+"Rs");
                                cost_graphs.setText(cst_graph+"Rs");
                                cost_index.setText(cst_index+"Rs");
                                total.setText(total_cost+"Rs");
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Items is not Entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot data = task.getResult();
                            if (data.exists()) {
                                Map<String, Object> map = data.getData();
                                String username = (String) map.get("Name");
                                name.setText(username);
                                String Rollno = (String) map.get("Roll_no");
                                roll_no.setText(Rollno);
                                String sem = (String) map.get("Semester");
                                semester.setText(sem);
                            }
                        }
                    }
                });

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
