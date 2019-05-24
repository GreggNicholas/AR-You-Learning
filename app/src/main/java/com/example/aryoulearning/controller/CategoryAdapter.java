package com.example.aryoulearning.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.view.MainActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<List<Model>> categoryList;
    private List<String> categoryName;
    private NavListener listener;


    public CategoryAdapter(List<List<Model>> categoryList, List<String> categoryName) {
        this.categoryList = categoryList;
        this.categoryName = categoryName;
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
        holder.onBind(categoryList.get(position), categoryName.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }

        public void onBind(final List<Model> categoryList, final String category, final NavListener listener) {
            categoryName.setText(category);
            categoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //listener.moveToGameFragment(categoryList, MainActivity.AR_SWITCH_STATUS);
                    listener.moveToHintFragment(categoryList, MainActivity.AR_SWITCH_STATUS);

                }
            });
        }
    }

}
