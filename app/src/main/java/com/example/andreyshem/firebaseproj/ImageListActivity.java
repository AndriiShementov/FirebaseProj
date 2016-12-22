package com.example.andreyshem.firebaseproj;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import static com.example.andreyshem.firebaseproj.CategoryListFragment.selectedCategoryName;


public class ImageListActivity extends AppCompatActivity {
    private static final int REQUEST = 1;
    private StorageReference mStorageRef;
    private static final String TAG = "Storage#DownloadService";

    private String fileName;
    private SQLiteOpenHelper localDataBase;
    private SQLiteDatabase db;
    private StorageReference islandRef;
    private String imgDescription;
    public static Bitmap[] imgArrays;
    private CaptionedImagesAdapter adapter;
    private int i;
    private Cursor cursor;

    private File[] localFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        setTitle(selectedCategoryName);

        try {
            localDataBase = new LocalDataBase(this);
            db = localDataBase.getWritableDatabase();

            cursor = db.query("IMG_SAVER",new String[] {"NAME_IMG"}, "NAME_CATEGORY = ?", new String[]{selectedCategoryName}, null,null,null);

            imgArrays = new Bitmap[cursor.getCount()];
            localFile = new File[cursor.getCount()];
            i = 0;
            if (cursor.moveToFirst()){
                do{
                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    String path = selectedCategoryName + "/" + cursor.getString(0);
                    islandRef = mStorageRef.child(path);

                    try {
                        localFile[i] = File.createTempFile("images", "jpg");
                        islandRef.getFile(localFile[i]).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                for (int i=0; i<cursor.getCount(); i++){
                                    String filePath = localFile[i].getPath();
                                    imgArrays[i] = BitmapFactory.decodeFile(filePath);
                                }
                                adapter = new CaptionedImagesAdapter(imgDescription,imgArrays);

                                imgDescription = "Some description"; // for each pictures

                                RecyclerView imgRecycler = (RecyclerView) findViewById (R.id.images_recycler);
                                imgRecycler.setAdapter(adapter);
                                GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
                                imgRecycler.setLayoutManager(layoutManager);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    i++;
                }while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        }catch (SQLiteException e){
            Log.d(TAG,"Database unavailable");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_photo, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap img = null;

        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String path = selectedImage.getPath();
            File filepath = new File(path);
            fileName = filepath.getName();

            // insert the record to local database
            localDataBase = new LocalDataBase(this);
            db = localDataBase.getWritableDatabase();
            //db.delete("IMG_SAVER", null, null);

            ContentValues imgValues = new ContentValues();
            imgValues.clear();
            imgValues.put("NAME_CATEGORY", selectedCategoryName);
            imgValues.put("NAME_IMG", fileName);
            db.insert("IMG_SAVER", null, imgValues);

            Cursor cursor = db.query("IMG_SAVER",
                    null,null,null,null,null,null);
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
                    String s = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            db.close();

            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

             //Load image to db
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://fbproj-72c82.appspot.com/");
            StorageReference riversRef = storageRef.child(selectedCategoryName + "/" + selectedImage.getLastPathSegment());//)

            UploadTask uploadTask = riversRef.putFile(selectedImage);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.i("Load","Picture unsuccessful uploads");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("Load","Picture successful uploads");
                    String toastText = "The picture added, the next time you can see it";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
