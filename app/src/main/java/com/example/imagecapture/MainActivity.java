package com.example.imagecapture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import java.io.IOException;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button captureButton, uploadButton;
    ImageView displayImage;
    TextView tempText;
    Spinner categoryDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureButton);
        displayImage = findViewById(R.id.displayImage);
        uploadButton = findViewById(R.id.uploadButton);
        tempText = findViewById(R.id.tempText);
        Spinner categoryDropdown = findViewById(R.id.categoryDropdown);


        //Camera Permission for first time user
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA}, 100);
        }


        //Defining Dropdown Categories
        String[] dropdownItems = new String[]{"Food", "Travel", "Nature", "Vehicles", "Electronics", "Aesthetics"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        categoryDropdown.setAdapter(adapter);


        //OnClick Listener for Capture Button
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, 100);
                captureButton.setVisibility(view.INVISIBLE);
                categoryDropdown.setVisibility(view.VISIBLE);
                uploadButton.setVisibility(view.VISIBLE);
            }
        });


        //OnClick Listener for Upload Button
        String flaskURL = "http://127.0.0.1:5001/";
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postRequest("GET",flaskURL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                displayImage.setImageBitmap(bitmap);
            }
        }
    }


    void postRequest(String type, String flaskURL) {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request
//                .Builder()
////                        .post(requestBody)
//                .url(flaskURL)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(final Call call, final IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("Done", "reponse");
//                        tempText.setText("Done!");
//                    }
//                });
//            }
//        });
    }
}






