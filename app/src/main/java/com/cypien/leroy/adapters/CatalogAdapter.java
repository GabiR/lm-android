package com.cypien.leroy.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Catalog;
import com.cypien.leroy.utils.Connections;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    private List<Catalog> cataloage;
    private Context context;
    private boolean notCached = true;

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
        LeroyApplication.getCacheManager().put("catalog_nr", cataloage.size()+1);
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cataloage_pager_item, parent, false);

        return new CatalogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CatalogViewHolder holder, final int position) {
        final Catalog catalog = cataloage.get(position);

            if (Connections.isNetworkConnected(context)) {

                Picasso.with(context).load(catalog.getCoverImageURL()).fit().into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                        if (notCached) {
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageView.getDrawable();
                            catalog.buildImageBase(bitmapDrawable.getBitmap());
                            LeroyApplication.getCacheManager().put("catalog_" + (position + 1), catalog);
                            if (position == getItemCount() - 1)
                                notCached = false;
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            } else {
                holder.imageView.setImageBitmap(catalog.getCover());

            }

      /*  Picasso.with(context).load(catalog.getCoverImageURL()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.imageView.setImageBitmap(bitmap);
                cataloage.get(position).buildImageBase(bitmap);
                LeroyApplication.getCacheManager().put("catalog_"+(position+1), cataloage.get(position));

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return cataloage.size();
    }
}
