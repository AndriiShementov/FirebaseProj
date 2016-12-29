package com.example.andreyshem.firebaseproj;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;


/**
 * Created by andreyshem on 28.12.2016.
 */

public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private StorageReference[] storageReference;
    private ProgressDialog pd;

    public ImageListAdapter(Context context, StorageReference[] storageReference, Activity activity) {
        super(context, R.layout.grid_view_item_image, storageReference);

        this.context = context;
        this.storageReference = storageReference;

        inflater = LayoutInflater.from(context);

        pd = new ProgressDialog(activity);
        pd.setTitle(R.string.open_page);
        pd.setMessage(context.getString(R.string.please_wait));
        pd.show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_view_item_image, parent, false);
        }

        Glide
                .with(context)
                .using(new FirebaseImageLoader())
                .load(storageReference[position])
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
               // .error(R.mipmap.ic_launcher) // will be displayed if the image cannot be loaded
                .into((ImageView) convertView);
        pd.dismiss();
        return convertView;
    }

}
