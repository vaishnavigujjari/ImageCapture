package com.example.imagecapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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

    private TextView textView_response;

    private String url = "http://192.168.0.32:5000/";
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

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*making a post request.*/
        captureButton.setOnClickListener(view -> {
                startActivityForResult(captureIntent, 100);
                captureButton.setVisibility(view.INVISIBLE);
                categoryDropdown.setVisibility(view.VISIBLE);
                uploadButton.setVisibility(view.VISIBLE);
        });

        uploadButton.setOnClickListener(view -> {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            byte[] byteArray = new byte[0];//captureIntent.getByteArrayExtra("data");
            multipartBodyBuilder.addFormDataPart("image", "Android_Flask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
            RequestBody postBodyImage = multipartBodyBuilder.build();
            sendRequest(postBodyImage);
        });

    }
    void sendRequest(RequestBody requestBody) {

        /* if url is of our get request, it should not have parameters according to our implementation.
         * But our post request should have 'name' parameter. */
        String fullURL = url;
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        /* If it is a post request, then we have to pass the parameters inside the request body*/
        request = new Request.Builder()
                .url(fullURL)
                .post(requestBody)
                .build();

        /* this is how the callback get handled */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

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