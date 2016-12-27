package com.example.andreyshem.firebaseproj;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryListFragment extends Fragment implements AdapterView.OnItemClickListener {

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

        mListAdapter.clear();

        // Read from the database and update ListAdapter
        FirebaseDB firebaseDB = new FirebaseDB();
        firebaseDB.recordToLstVw(mListAdapter);

        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
