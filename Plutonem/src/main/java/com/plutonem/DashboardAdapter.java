package com.plutonem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private List<DataModel> dataModelList;
    private Context mContext;

    // View holder class whose objects represent each list item
    public static class DashboardViewHolder extends RecyclerView.ViewHolder {


//        private mRandom = Random()
//        private ImageView cardImageView;

            private ImageView commodityCardImageView;

//        private TextView titleTextView;
            private TextView commodityDescriptionTextView;
            private Random randomHeight = new Random();

        private DashboardViewHolder(@NonNull View itemView) {
            super(itemView);

//            commodityCardImageView = itemView.findViewById(R.id.imageView);

//            cardImageView = itemView.findViewById(R.id.imageView);
//            titleTextView = itemView.findViewById(R.id.card_title);
//            commodityDescriptionTextView = itemView.findViewById(R.id.card_subtitle);
        }

        private void bindData(DataModel dataModel, Context context) {

//            cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dashboard_1));
//            titleTextView.setText(dataModel.getTitle());
            commodityDescriptionTextView.setText(dataModel.getCommodityDescription());

            commodityCardImageView.getLayoutParams().height = dpToPx(getRandomIntInRange(300,180), context);
//            commodityCardImageView.getLayoutParams().height = (int)context.getResources().getDimension(R.dimen.imageview_height);
        }

        private int getRandomIntInRange(int max, int min) {

            return randomHeight.nextInt(max - min + 1) + min;
        }

        public int dpToPx(int dp, Context context) {

            float density = context.getResources()
                    .getDisplayMetrics()
                    .density;
            return Math.round((float) dp * density);
        }
    }

    public DashboardAdapter(List<DataModel> modelList, Context context) {

        dataModelList = modelList;
        mContext = context;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate out card list item

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hpage_card, parent, false);
        // Return a new view holder

        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

        // Bind data for the item at position
        holder.bindData(dataModelList.get(position), mContext);
    }

    @Override
    public int getItemCount() {

        // Return the total number of items
        return dataModelList.size();
    }

//    RecyclerView list gets shuffled and changing position of items inside the adapter on scrolling fast in Android application.
//    The solution is very simple you Just have to override two methods in your Adapter class and it will solve your issue.
//    The reason is because some other overridden methods need to call these override methods while rendering the list items.

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

