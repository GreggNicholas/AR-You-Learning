package com.capstone.aryoulearning.controller;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.view.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<List<Model>> categoryList;
    private List<String> categoryName;
    private List<String> categoryImages;
    private NavListener listener;


    public CategoryAdapter(List<List<Model>> categoryList, List<String> categoryName, List<String> categoryImages) {
        this.categoryList = categoryList;
        this.categoryName = categoryName;
        this.categoryImages = categoryImages;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.category_item, viewGroup, false);

        Context context = viewGroup.getContext();
        if (context instanceof NavListener) {
            listener = (NavListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement FragmentInteractionListener");
        }
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.onBind(categoryList.get(position), categoryName.get(position), categoryImages.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private CardView categoryCard;
        private TextView categoryName;
        private ImageView categoryImage;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryCard = itemView.findViewById(R.id.category_card);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_image);
        }

        public void onBind(final List<Model> categoryList, final String category, final String backgroundImage, final NavListener listener) {
            categoryName.setText(category);
            Picasso.get()
                    .load(backgroundImage)
                    .into(categoryImage);

            Log.d("TAG", backgroundImage);
            categoryCard.setOnClickListener(v -> {
                MainActivity.currentCategory = category;
                listener.moveToHintFragment(categoryList);
                makeVibration();
            });
        }

        private void makeVibration() {
            Vibrator categoryVibrate = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            categoryVibrate.vibrate(100);
        }
    }
}
