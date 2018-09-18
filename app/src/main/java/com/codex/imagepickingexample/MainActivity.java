package com.codex.imagepickingexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    Uri uri;
    private ImageView imageView;
    private String imageData = null;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 4;
    static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    static final int STORAGE_PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);

    }

    public void takeImage(View view) {
        getCameraPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if(cameraIntent.resolveActivity(getPackageManager())!=null){
           startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
       }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            Bitmap imageBitmap = (Bitmap) bundle.get("data");
            imageData = encodeTobase64(imageBitmap, Bitmap.CompressFormat.JPEG, 50);
            imageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            uri = data.getData();
            //imageView.setImageURI(uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                if (bitmap.getByteCount()>0){
                    Toast.makeText(this, String.valueOf(bitmap.getByteCount()), Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);
                }else {
                    Toast.makeText(this, "Sucks", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void getCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            }
        }

    }

    public static String encodeTobase64(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

    }

    public static Bitmap decodeToBitmap(String string) {

        byte[] decodeBytes = Base64.decode(string, 0);
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);

    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }


    public void takeGalleryImage(View view) {
        pickImageFromGallery();
    }
}
