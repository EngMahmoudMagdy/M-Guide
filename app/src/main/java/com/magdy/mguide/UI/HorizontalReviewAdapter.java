package com.magdy.mguide.UI;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magdy.mguide.R;

import java.util.List;

/**
 * Created by engma on 5/24/2017.
 */

 class HorizontalReviewAdapter extends RecyclerView.Adapter<HorizontalReviewAdapter.SimpleViewHolder> {


    private Context context;
    private List <String>nameList;
    private List <String>reviewList;
     HorizontalReviewAdapter(Context context, List<String> names , List<String> reviews ){
        this.context = context;
        this.nameList = names ;
        this.reviewList = reviews ;
    }

     class SimpleViewHolder extends RecyclerView.ViewHolder {
         TextView review , name ;
         CardView cardView;
         SimpleViewHolder(View itemView) {
            super(itemView);
            review =(TextView)itemView.findViewById(R.id.review_body);
            name =(TextView)itemView.findViewById(R.id.review_name);
            cardView = (CardView)itemView.findViewById(R.id.review_item);
        }
    }
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new HorizontalReviewAdapter.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        final String name = nameList.get(position);
        final String review = reviewList.get(position);
        holder.name.setText(name);
        holder.review.setText(review);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReviewDetailActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("review",review);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


}
