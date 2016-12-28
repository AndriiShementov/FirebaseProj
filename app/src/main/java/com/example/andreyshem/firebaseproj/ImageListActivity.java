package com.example.andreyshem.firebaseproj;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.andreyshem.firebaseproj.data_bases.FirebaseDB;
import com.example.andreyshem.firebaseproj.data_bases.LocalDataBase;

import java.io.File;

import static com.example.andreyshem.firebaseproj.CategoryListFragment.selectedCategoryName;
import static com.example.andreyshem.firebaseproj.data_bases.FirebaseDB.commonRef;

public class ImageListActivity extends AppCompatActivity {
    private static final int REQUEST = 1;
    private SQLiteOpenHelper localDataBase;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activty_image_grid_view);
        setTitle(selectedCategoryName);

        initialFDB();

        FirebaseDB firebaseDB = new FirebaseDB();
        firebaseDB.setImgArrays(db, this);

        GridView gridView = (GridView)findViewById(R.id.usage_gridview);
        gridView.setAdapter(new ImageListAdapter(ImageListActivity.this, commonRef, this));

//        <===============  Using CaptionedImageAdaptor =================================>
//        setContentView(R.layout.activity_image_list);
//        RecyclerView imgRecycler = (RecyclerView) findViewById (R.id.images_recycler);
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
//        firebaseDB.downloadImages( db, this, imgRecycler,layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_photo, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            String path = selectedImage.getPath();
            File filepath = new File(path);
            String fileName = filepath.getName();

            // insert the record to local database
            initialFDB();

            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.recordImageInDB(db, fileName, selectedImage);

        }
        super.onActivityResult(requestCode, resultCode, data);
        this.recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_picture){
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initialFDB(){
        localDataBase = new LocalDataBase(this);
        db = localDataBase.getWritableDatabase();
    }
}
