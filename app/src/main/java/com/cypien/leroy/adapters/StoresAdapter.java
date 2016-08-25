package com.cypien.leroy.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Store;

import java.util.List;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class StoresAdapter extends  RecyclerView.Adapter<StoresAdapter.StoresListViewHolder> {
    private List<Store> storesList;

    public class StoresListViewHolder extends RecyclerView.ViewHolder {
        public TextView store_name;
        public TextView store_address;

        public StoresListViewHolder(View view) {
            super(view);
            store_name = (TextView) view.findViewById(R.id.store_name);
            store_address = (TextView) view.findViewById(R.id.store_address);
        }
    }


    public StoresAdapter(List<Store> storesList) {
        this.storesList = storesList;
    }

    @Override
    public StoresListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stores_list_row, parent, false);

        return new StoresListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoresListViewHolder holder, int position) {
        Store store = storesList.get(position);
        holder.store_name.setText(store.getName());
        holder.store_address.setText(store.getAddress());
    }

    @Override
    public int getItemCount() {
        return storesList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
