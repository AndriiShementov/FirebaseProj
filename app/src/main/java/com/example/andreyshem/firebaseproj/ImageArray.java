package com.example.andreyshem.firebaseproj;

/**
 * Created by andreyshem on 14.12.2016.
 */

public class ImageArray {

    private String imageDescription;
    private int intImage;

    public static final ImageArray[] imgArray = {
            new ImageArray("picture", R.drawable.pic1),
            new ImageArray("picture", R.drawable.pic2),
            new ImageArray("picture", R.drawable.pic3),
            new ImageArray("picture", R.drawable.pic4),
            new ImageArray("picture", R.drawable.pic5),
            new ImageArray("picture", R.drawable.pic6),
            new ImageArray("picture", R.drawable.pic8)
    };

    private ImageArray(String imageDescription, int intImage){
        this.imageDescription = imageDescription;
        this.intImage = intImage;
    }

    public String getImageDescription(){
        return imageDescription;
    }

    public int getIntImage(){
        return intImage;
    }

    public int setIntImage(){
        new ImageArray("picture", R.drawable.pic8);
        return intImage;
    }
}
