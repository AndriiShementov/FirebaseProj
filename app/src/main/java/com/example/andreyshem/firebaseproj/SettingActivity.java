package com.example.andreyshem.firebaseproj;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;





import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;


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

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("title");

            editTextName = (EditText) findViewById(R.id.editTxtName);
            editTextDescript = (EditText) findViewById(R.id.editTxtDescr);

            String mName = editTextName.getText().toString();
            String descr = editTextDescript.getText().toString();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(mName,descr);

            myRef.updateChildren(childUpdates);

            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
