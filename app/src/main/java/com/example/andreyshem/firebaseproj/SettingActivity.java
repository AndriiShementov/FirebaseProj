package com.example.andreyshem.firebaseproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.andreyshem.firebaseproj.data_bases.FirebaseDB;

public class SettingActivity extends AppCompatActivity {

    //private DatabaseReference mDatabase;
    private EditText editTextName;
    private EditText editTextDescript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_category){

            editTextName = (EditText) findViewById(R.id.editTxtName);
            editTextDescript = (EditText) findViewById(R.id.editTxtDescr);

            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.recordToDB(editTextName, editTextDescript);

            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
