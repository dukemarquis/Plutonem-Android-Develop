package com.plutonem;

import com.plutonem.R;

public class DataModel {

    private int imageDrawable;
//    private String title;
//    private String subTitle;
    private String commodityDescription;

    public DataModel(int id) {
        imageDrawable = R.drawable.flower;
//        title = String.format(Locale.ENGLISH, "Title %d Goes Here", id);
//        subTitle = String.format(Locale.ENGLISH, "Sub title %d goes here", id);
        commodityDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

//    public String getTitle() {
//        return title;
//    }

//    public String getSubTitle() {
//        return subTitle;
//    }

    public String getCommodityDescription() {
        return commodityDescription;
    }
}