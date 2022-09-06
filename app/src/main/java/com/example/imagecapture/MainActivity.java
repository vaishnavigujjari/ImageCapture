package com.example.imagecapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    Button captureButton, uploadButton;
    ImageView displayImage;
    TextView tempText;
    Spinner categoryDropdown;
    String currentPhotoPath;

    private TextView textView_response;

    private String url = "http://10.153.96.6:5000";
    private String POST = "POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureButton);
        displayImage = findViewById(R.id.displayImage);
        uploadButton = findViewById(R.id.uploadButton);
        tempText = findViewById(R.id.tempText);
        Spinner categoryDropdown = findViewById(R.id.categoryDropdown);

        //Defining Dropdown Categories
        String[] dropdownItems = new String[]{"Food", "Travel", "Nature", "Vehicles", "Electronics", "Aesthetics"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        categoryDropdown.setAdapter(adapter);

        final Intent[] uploadIntent = {new Intent()};

        /*making a post request.*/
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uploadIntent[0] = captureIntent;
                startActivityForResult(captureIntent, 100);
                captureButton.setVisibility(view.INVISIBLE);
                categoryDropdown.setVisibility(view.VISIBLE);
                uploadButton.setVisibility(view.VISIBLE);
            }
        });

        uploadButton.setOnClickListener(view -> {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            byte[] byteArray = uploadIntent[0].getByteArrayExtra("data");
            byteArray = new byte[0];
            multipartBodyBuilder.addFormDataPart("image", "Android_Flask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
            RequestBody postBodyImage = multipartBodyBuilder.build();
            sendRequest(postBodyImage);
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("photo file successfully created");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 102);
            }
        }
    }

    void sendRequest(RequestBody requestBody) {

        String fullURL = url;
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        request = new Request.Builder()
                .url(fullURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.tempText);
                        try {
                            responseText.setText("Server's Response\n" + response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}