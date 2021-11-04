package com.example.c964_capstone_java;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.c964_capstone_java.classifier.ImageClassifier;
import com.example.c964_capstone_java.*;

import org.tensorflow.lite.schema.Model;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class IdentifyActivity extends AppCompatActivity {

    private static final int IMG_REQUEST_CODE = 7;
    private Button selectBtn;
    private Button classifyBtn;
    private ImageView imageView;
    private ListView listView;
    Uri imageUri;
    private Bitmap imageBitmap;
    private ImageClassifier imageClassifier;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        initializeUIElements();
        
    }

    // All User Interface Elements
    public void initializeUIElements(){
        selectBtn = findViewById(R.id.selectBtn);
        classifyBtn = findViewById(R.id.classifyBtn);
        classifyBtn = findViewById(R.id.classifyBtn);
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.listView);

        try {
            imageClassifier = new ImageClassifier(this);
        } catch (IOException e) {
            Log.e("imageClassifier error", "Error creating image classifier");
        }

        // Select button listener to prompt user to select photo from their gallery
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMG_REQUEST_CODE);
            }
        });

    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, IMG_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMG_REQUEST_CODE){
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(imageBitmap);
            // Pass this bitmap to classifier
            /*
            List<ImageClassifier.Recognition> predictions = imageClassifier.recognizeImage(imageBitmap, 0);
            final List<String> predictionsList = new ArrayList<>();
            for(ImageClassifier.Recognition recog : predictions) {
                predictionsList.add(recog.getName() + "       Confidence: " + recog.getConfidence());
            }
            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>
                    (this, R.layout.support_simple_spinner_dropdown_item, predictionsList);
            listView.setAdapter(predictionsAdapter);

            Log.d( "PREDICTION", predictionsList.toString());

             */
        }
    }

    public void setTextField(View view) {
        List<ImageClassifier.Recognition> predictions = imageClassifier.recognizeImage(imageBitmap);
        final List<String> predictionsList = new ArrayList<>();
        for(ImageClassifier.Recognition recog : predictions) {
            predictionsList.add("Label: " + recog.getName() + "       Confidence: " + recog.getConfidence());
        }
        ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>
                (this, R.layout.support_simple_spinner_dropdown_item, predictionsList);
        listView.setAdapter(predictionsAdapter);

    }


}