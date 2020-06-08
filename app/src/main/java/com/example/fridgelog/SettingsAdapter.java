package com.example.fridgelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingsAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private CategoriesList categoriesList;
    private String[] categories;

    public SettingsAdapter (Context c, CategoriesList categoriesList)
    {
        this.categoriesList = categoriesList;
        //Gets an array containing the categories
        categories = this.categoriesList.toArray();
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return categories.length;
    }
    @Override
    public Object getItem(int position)
    {
        return categories[position];
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = mInflater.inflate(R.layout.settingsrow,null);
        TextView category = v.findViewById(R.id.category);
        //Sets each of the individual TextViews to the category according to the index
        category.setText(categories[position]);
        return v;
    }
}
