package com.example.andreyshem.firebaseproj;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment {

    ArrayAdapter<String> mListAdapter;
    public static String selectedCategoryName;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_list,
                R.id.categoryTextView,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        final ListView mListView = (ListView) rootView.findViewById(R.id.categoryListView);

        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(mListView.getItemAtPosition(position).toString());
                selectedCategoryName = selectedFromList.substring(0,selectedFromList.indexOf(" ("));
                startActivity(new Intent(getActivity(), ImageListActivity.class));
            }
        });

        // Read from the database and update ListAdapter
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("title");

        mListAdapter.clear();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Create records according Firebase
                    String ss = postSnapshot.getKey()+ " (" + postSnapshot.getValue() + ")";
                    mListAdapter.add(ss);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return rootView;
    }



}
