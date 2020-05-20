package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrCodegen extends AppCompatActivity {

    private ImageView qrcode;
    String UserId;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    String TAG = "generate qr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_codegen);

        qrcode = findViewById(R.id.qrcode);
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        WindowManager manager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerdimension = width<height ?width:height;
        smallerdimension = smallerdimension * 3/4;
        qrgEncoder = new QRGEncoder(UserId, null, QRGContents.Type.TEXT,smallerdimension);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG,e.toString());
        }


    }
}
