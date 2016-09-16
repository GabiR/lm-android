package com.cypien.leroy.adapters;/*
 * Created by Alex on 02.09.2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Catalog;

import java.util.List;

public class CatalogListAdapter extends RecyclerView.Adapter<CatalogListAdapter.CatalogListViewHolder> {

    private List<Catalog> catalogs;
    private Context context;

    public CatalogListAdapter(List<Catalog> catalogs, Context context) {
        this.catalogs = catalogs;
        this.context = context;
    }

    @Override
    public CatalogListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalog_list_row, parent, false);

        return new CatalogListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CatalogListViewHolder holder, int position) {
        holder.catalog_title.setText(catalogs.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return catalogs.size();
    }

    public class CatalogListViewHolder extends RecyclerView.ViewHolder {

        public TextView catalog_title;

        public CatalogListViewHolder(View view) {
            super(view);

            catalog_title = (TextView) view.findViewById(R.id.catalog_title);
        }
    }
}