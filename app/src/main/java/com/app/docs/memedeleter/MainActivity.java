package com.app.docs.memedeleter;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String memeDir = "MemeDeleterFolder";
    private ArrayList<String> images;
    File[] fileList;
    private int imageCount;
    private GridViewAdapter gridViewAdapter;
    private LayoutInflater inflater;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMemeDir();
        images = new ArrayList<String>();
        loadImagePaths();

        gridView = (GridView) findViewById(R.id.grid_view);
        gridViewAdapter = new GridViewAdapter(images, MainActivity.this);
        gridView.setAdapter(gridViewAdapter);

        new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.loadClassifier(MainActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    private void loadImagePaths() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), memeDir);
        Log.e("FOLDER PATH", file.getAbsolutePath());
        if (file.isDirectory()) {
            fileList = file.listFiles();
            for (int i = 0; i < fileList.length; ++i) {
                images.add(fileList[i].getAbsolutePath());
            }
        }
    }

    private void createMemeDir() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), memeDir);

        if (folder.exists()) {
            if (folder.isDirectory()) {
            //    Do Nothing
            } else {
            //    A file with this name exists. Which is a problem
            }
        } else {
        //Folder does not exist. Create it.
            boolean status = folder.mkdir();
            if (status) {
            //    Created successfully
            } else {
            //    Failed
            }
        }
    }

    public void itemClicked(int position) {
        String imagePath = images.get(position);
        Log.d("MainClick", imagePath);
        if (Utils.isMeme(imagePath)) {
            Toast.makeText(this, "This is not a meme", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "This is a meme", Toast.LENGTH_SHORT).show();
        }
//        images.remove(position);
//        gridViewAdapter.notifyDataSetChanged();
//        gridView.invalidateViews();
    // DELETE FILE HERE

    }
}
