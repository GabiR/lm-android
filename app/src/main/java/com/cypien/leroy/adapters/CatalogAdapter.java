package com.cypien.leroy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Catalog;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    private List<Catalog> cataloage;
    private Context context;

    public class CatalogViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public CatalogViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }


    public CatalogAdapter(List<Catalog> cataloage, Context context) {
        this.cataloage = cataloage;
        this.context = context;
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cataloage_pager_item, parent, false);

        return new CatalogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CatalogViewHolder holder, int position) {
        Catalog catalog = cataloage.get(position);
        Picasso.with(context).load(catalog.getCoverImageURL()).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cataloage.size();
    }
}
