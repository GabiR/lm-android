package com.cypien.leroy.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Category;
import java.util.ArrayList;

/**
 * Created by Alex on 9/16/2015.
 */
public class CategoriesAdapter extends ArrayAdapter<Category> {

    private final ArrayList<Category> list;
    private final Activity context;

    public CategoriesAdapter(Activity context, ArrayList<Category> list) {
        super(context, R.layout.category, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CheckBox checkbox;
        TextView categoryName;
        Category cat = list.get(position);
        LayoutInflater inflator = context.getLayoutInflater();
        View  view = inflator.inflate(R.layout.category, null);
        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        categoryName = (TextView) view.findViewById(R.id.category_name);
        checkbox.setTag(cat);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Category cat = (Category) checkbox.getTag();
                cat.setSelected(buttonView.isChecked());
            }
        });
        categoryName.setText(cat.getName());
        checkbox.setChecked(cat.isSelected());
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Category getItem(int position) {
        return list.get(position);
    }

    public String getSelectedCategoriesIndexes(){
        String categories="";
        for(Category cat: list){
            if(cat.isSelected()){
                categories+=(list.indexOf(cat)+1)+",";
            }
        }
        if (categories.length() > 0) {
            categories = categories.substring(0, categories.length()-1);
        }
        return categories;
    }

    public String getSelectedCategories(){
        String categories="";
        for(Category cat: list){
            if(cat.isSelected()){
                categories+=cat.getName()+", ";
            }
        }
        if (categories.length() > 0) {
            categories = categories.substring(0, categories.length()-2);
        }
        return categories;
    }
}
