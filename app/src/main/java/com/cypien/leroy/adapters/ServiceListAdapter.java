package com.cypien.leroy.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Service;

import java.util.List;

/**
 * Created by GabiRotaru on 03/08/16.
 */
public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServicesListViewHolder> {

    private List<Service> serviceList;
    private Context context;

    public class ServicesListViewHolder extends RecyclerView.ViewHolder {
        public ImageView service_icon;
        public TextView service_title;

        public ServicesListViewHolder(View view) {
            super(view);
            service_icon = (ImageView) view.findViewById(R.id.service_icon);
            service_title = (TextView) view.findViewById(R.id.service_title);
        }
    }


    public ServiceListAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
    }

    @Override
    public ServicesListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.services_list_row, parent, false);

        return new ServicesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServicesListViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.service_icon.setImageDrawable(ContextCompat.getDrawable(context, service.getIcon()));
        holder.service_title.setText(service.getName());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}