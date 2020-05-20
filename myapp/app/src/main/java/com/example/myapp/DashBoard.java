package com.example.myapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import androidmads.library.qrgenearator.QRGEncoder;


public class DashBoard extends AppCompatActivity  {
    private ArrayList<ExampleItem> mExampleList;
    DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private int backpressed = 0;
    FirebaseFirestore fstore;
    String userID;
    TextView user_name, user_email, user_sem, user_roll;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    private Button add, done;
    private Spinner prod_item;
    private EditText count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        createExampleList();
        buildRecyclerView();

        fstore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        add = findViewById(R.id.add_button);
        prod_item = findViewById(R.id.dash_item);
        count = findViewById(R.id.dash_total);
        done = findViewById(R.id.dash_done);

        final DocumentReference documentRef = fstore.collection("Items").document(userID);
        final Map<String, Object> items = new HashMap<>();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product_item = prod_item.getSelectedItem().toString().trim();
                String total = count.getText().toString().trim();
                if (product_item.isEmpty()){
                    Toast.makeText(DashBoard.this, "Select an item", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (total.isEmpty()){
                    Toast.makeText(DashBoard.this, "Enter the count", Toast.LENGTH_SHORT).show();
                    return;
                }
                items.put(product_item, total);
                documentRef.set(items);
                mExampleList.add(new ExampleItem(product_item,total));
                mAdapter.notifyDataSetChanged();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExampleList.isEmpty()){
                    Toast.makeText(DashBoard.this, "Enter some items", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(DashBoard.this, QrCodegen.class));
                finish();
            }
        });

        final DocumentReference documentReference = fstore.collection("Users").document(userID);

        FirebaseUser user = mAuth.getCurrentUser();
        if ( user == null) {
            startActivity(new Intent(DashBoard.this, LoginActivity.class));
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlack));


        NavigationView nav_view = findViewById(R.id.nav_view);

//        nav_view.setCheckedItem(R.id.search);
        View headerView = nav_view.getHeaderView(0);

        user_name =headerView.findViewById(R.id.user_name);
        user_email = headerView.findViewById(R.id.user_email);
        user_sem = headerView.findViewById(R.id.user_sem);
        user_roll = headerView.findViewById(R.id.user_roll);
        user_email.setText(mAuth.getCurrentUser().getEmail());


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId())
                {
                    case R.id.home:
                        Toast.makeText(DashBoard.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logout:
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(DashBoard.this, LoginActivity.class));
                        break;
                    case R.id.menu_clear:
                        mExampleList.clear();
                        mAdapter.notifyDataSetChanged();
                        fstore.collection("Items").document(userID).delete();
                        break;
                    case R.id.menu_profile:
                        startActivity(new Intent(DashBoard.this, setProfile.class));
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if (data.exists()) {
                        Map<String, Object> map = data.getData();
                        String username = (String) map.get("Name");
                        user_name.setText(username);
                        String Rollno = (String) map.get("Roll_no");
                        user_roll.setText(Rollno);
                        String sem = (String) map.get("Semester");
                        user_sem.setText(sem);
                    }
                }
                else{
                    startActivity(new Intent(DashBoard.this, setProfile.class));
                }
            }
        });
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recylcer_view);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void createExampleList() {
        mExampleList = new ArrayList<>();
    }


    @Override
    public void onBackPressed() {
        backpressed++;
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
        if (backpressed == 1) {
            Toast.makeText(DashBoard.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        if (backpressed > 1) {
            finish();
        }
    }
}