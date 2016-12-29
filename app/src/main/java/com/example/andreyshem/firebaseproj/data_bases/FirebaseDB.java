package com.example.andreyshem.firebaseproj.data_bases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andreyshem.firebaseproj.CaptionedImagesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.andreyshem.firebaseproj.CategoryListFragment.selectedCategoryName;

/**
 * Created by andreyshem on 27.12.2016.
 */

public class FirebaseDB {
    private StorageReference islandRef;
    private StorageReference mStorageRef;
    public static StorageReference[] commonRef;

    private static final String TAG = "Storage#DownloadService";
    private static final String FIREBASE_URL = "gs://fbproj-72c82.appspot.com/";
    private static final String NAME_IMAGE = "NAME_IMG";
    private static final String NAME_CATEGORY = "NAME_CATEGORY";
    private static final String NAME_TABLE = "IMG_SAVER";
    private static final String NAME_TABLE_OF_FDB = "title";

    public static Bitmap[] imgArrays = null;
    private File[] localFile = null;

    private Cursor cursor;
    private CaptionedImagesAdapter adapter;
    private String imgDescription;
    private ProgressDialog pd;

    private int i;

    public FirebaseDB(){}

    //   ================================ new approach for download =======================>
    public void setImgArrays (SQLiteDatabase db, final Activity activity){

        try {
            cursor = db.query( NAME_TABLE,new String[] {NAME_IMAGE}, NAME_CATEGORY + " = ?", new String[]{selectedCategoryName}, null,null,null);
            commonRef = new StorageReference[cursor.getCount()];
            mStorageRef = FirebaseStorage.getInstance().getReference();
            i = 0;
            if (cursor.moveToFirst()){
                do{
                    String path = selectedCategoryName + "/" + cursor.getString(0);
                    commonRef[i] = mStorageRef.child(path);
                    i++;
                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Log.d(TAG,"Database unavailable");

            String toastText = "Database unavailable";
            Toast toast = Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

//   ================================ old approach for download =======================>
    public void downloadImages(SQLiteDatabase db,
                              final Activity activity, final RecyclerView imgRecycler,
                              final GridLayoutManager layoutManager){
        pd = new ProgressDialog(activity);
        pd.setTitle("Open page");
        pd.setMessage("Please wait ...");
        pd.show();

        try {
            cursor = db.query( NAME_TABLE,new String[] {NAME_IMAGE}, NAME_CATEGORY + " = ?", new String[]{selectedCategoryName}, null,null,null);
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
                                pd.dismiss();
                                imgDescription = "Some description"; // for each pictures
                                adapter = new CaptionedImagesAdapter(imgDescription,imgArrays);

                                imgRecycler.setAdapter(adapter);
                                imgRecycler.setLayoutManager(layoutManager);
                            }
                        });
                    } catch (IOException e) {
                        Log.d(TAG,"Error create temp file " + i);

                        String toastText = "Error of create a temporary file";
                        Toast toast = Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    i++;
                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            Log.d(TAG,"Database unavailable");

            String toastText = "Database unavailable";
            Toast toast = Toast.makeText(activity.getApplicationContext(), toastText, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void recordImageInDB(SQLiteDatabase db, String fileName, Uri selectedImage){

        ContentValues imgValues = new ContentValues();
        imgValues.clear();
        imgValues.put(NAME_CATEGORY, selectedCategoryName);
        imgValues.put(NAME_IMAGE, fileName);
        db.insert(NAME_TABLE, null, imgValues);
        db.close();

        //Load image to db
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_URL);
        StorageReference riversRef = storageRef.child(selectedCategoryName + "/" + selectedImage.getLastPathSegment());

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
            }
        });
    }

    public void recordToLstVw(final ArrayAdapter<String> mListAdapter){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NAME_TABLE_OF_FDB);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Create records according Firebase
                    String recordToListView = postSnapshot.getKey()+ " (" + postSnapshot.getValue() + ")";
                    mListAdapter.add(recordToListView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void recordToDB(EditText editTextName, EditText editTextDescript){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NAME_TABLE_OF_FDB);


        String mName = editTextName.getText().toString();
        String descr = editTextDescript.getText().toString();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(mName,descr);

        myRef.updateChildren(childUpdates);
    }

}
