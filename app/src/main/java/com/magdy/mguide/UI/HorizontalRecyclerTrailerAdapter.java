package com.magdy.mguide.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.magdy.mguide.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by engma on 5/24/2017.
 */

class HorizontalRecyclerTrailerAdapter extends RecyclerView.Adapter<HorizontalRecyclerTrailerAdapter.SimpleViewHolder> {

    List<String> itemList;
    List<String> linkList;
    private Context context;

    public HorizontalRecyclerTrailerAdapter(Context context, List<String> items, List<String> links) {
        this.context = context;
        this.itemList = items;
        this.linkList = links;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        final String item = itemList.get(position);
        Picasso.with(context).load(item).fit().into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkList.get(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        SimpleViewHolder(View itemView) {

            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

}
