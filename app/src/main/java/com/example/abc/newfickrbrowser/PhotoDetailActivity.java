package com.example.abc.newfickrbrowser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class PhotoDetailActivity extends BaseActivity {

    static final String PHOTO_TRANSFER = "PHOTO_TRANSFER";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    String[] listItems;
    Photo photo;
    ImageView photoImage;
    Drawable drawable;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        activateToolbar(true);

        listItems = getResources().getStringArray(R.array.shopping_item);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);



        Intent intent = getIntent();


        photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);

        if (photo != null){
            TextView photoTitle = (TextView)findViewById(R.id.photo_title);
            photoTitle.setText("Title: " + photo.getTitle());

            TextView photoTags = (TextView)findViewById(R.id.photo_tags);
            photoTags.setText("Tags: " + photo.getTags());

            TextView photoAuthor = (TextView)findViewById(R.id.photo_author);
            photoAuthor.setText( photo.getAuthor());

            TextView photoLink = (TextView) findViewById(R.id.photo_link);
            photoLink.setText("Link: " + photo.getLink());
            photoLink.setMovementMethod(LinkMovementMethod.getInstance());

            photoImage = (ImageView)findViewById(R.id.photo_image);
            Picasso.with(this).load(photo.getLink())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(photoImage);



            photoImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(PhotoDetailActivity.this,R.style.AlertDialog);
                    mBuilder.setTitle("Choose an item");
                    mBuilder.setItems(listItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    downloadImage();
                                    break;
                                case 1:
                                    shareImage();
                                    break;
                                case 2:
                                    shareImageLink();
                                    break;
                            }
                        }
                    });

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();

                    return false;
                }
            });


        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void downloadImage()
    {
        if(ActivityCompat.checkSelfPermission(PhotoDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(PhotoDetailActivity.this,"you should give permission",Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{},PERMISSION_REQUEST_CODE);
            Intent intent1=new Intent(PhotoDetailActivity.this,PhotoDetailActivity.class);
            startActivity(intent1);
        }
        else
        {
            SpotsDialog dialog = new SpotsDialog(PhotoDetailActivity.this);
            dialog.show();
            dialog.setMessage("downloading");
            String filename = UUID.randomUUID().toString() + ".jpg";
            Picasso.with(getBaseContext())
                    .load(photo.getLink())
                    .into(new SaveImageHelperClass(getBaseContext(), dialog, PhotoDetailActivity.this.getContentResolver(), filename, "image description"));
            Toast.makeText(PhotoDetailActivity.this, "your image downloaded", Toast.LENGTH_LONG).show();
        }

    }
    public void shareImage() {
        Uri bmpUri = getLocalBitmapUri(photoImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }  // ...sharing failed, handle error

        Toast.makeText(PhotoDetailActivity.this, "image share is selected", Toast.LENGTH_LONG).show();
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public void shareImageLink()
    {
        Intent share=new Intent(Intent.ACTION_SEND);
        String s=photo.getLink();
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT,"Flicker Image");
        share.putExtra(Intent.EXTRA_TEXT,s);
        startActivity(Intent.createChooser(share,"Share.."));
    }

}
