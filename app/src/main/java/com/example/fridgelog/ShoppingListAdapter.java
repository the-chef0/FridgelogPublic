package com.example.fridgelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShoppingListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ShoppingList shoppingList;
    private String[][] shoppingListItems;

    public ShoppingListAdapter (Context c, ShoppingList shoppingList)
    {
        this.shoppingList = shoppingList;
        //Gets an array containing the shopping list
        shoppingListItems = this.shoppingList.toArrays();
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return shoppingListItems[0].length;
    }
    @Override
    public Object getItem(int position)
    {
        return shoppingListItems[0][position];
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = mInflater.inflate(R.layout.shoppinglistrow,null);
        TextView name = v.findViewById(R.id.name);
        //Sets each of the individual TextViews to the category according to the index
        name.setText(shoppingListItems[0][position]);
        return v;
    }
}