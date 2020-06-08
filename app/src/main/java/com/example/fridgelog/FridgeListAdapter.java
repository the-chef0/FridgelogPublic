/*
These adapters inflate the view that I made for the individual list items and sets their TextViews
according to the index of the item that is being displayed
 */
package com.example.fridgelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class FridgeListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private FridgeList fridgeList;
    private String[][] fridgeItems;
    private View v;
    private final Calendar c = Calendar.getInstance();

    public FridgeListAdapter (Context c, FridgeList fridgeList)
    {
        this.fridgeList = fridgeList;
        fridgeItems = this.fridgeList.toArrays();
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    //These have to be overridden, no need to explain
    @Override
    public int getCount()
    {
        return fridgeItems[0].length;
    }
    @Override
    public Object getItem(int position)
    {
        return fridgeItems[0][position];
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        v = mInflater.inflate(R.layout.fridgelistrow,null);

        //Finds TextViews for displaying each of the variables of the FridgeItem
        TextView name = v.findViewById(R.id.name);
        TextView category = v.findViewById(R.id.category);
        TextView qty = v.findViewById(R.id.qty);
        TextView days = v.findViewById(R.id.days);
        TextView shelf = v.findViewById(R.id.shelf);

        //Gets the number of days between UNIX time and the current date
        int daysSinceEpoch = daysSinceEpoch();
        //Sets up the calendar object with the date that the FridgeItem has saved in it
        c.set(Calendar.DAY_OF_MONTH,Integer.parseInt(fridgeItems[3][position]));
        c.set(Calendar.MONTH,Integer.parseInt(fridgeItems[4][position]));
        c.set(Calendar.YEAR,Integer.parseInt(fridgeItems[5][position]));
        //Turns that into the number of days between the date on the FridgeItem and UNIX time
        int dayAddedSinceEpoch = (int)(c.getTimeInMillis()/86400000);
        //Subtracts the number of days set in the FridgeItem from the number of days of the current
        //date to calculate how many days it's been in the fridge
        String daysIn = String.valueOf(daysSinceEpoch - dayAddedSinceEpoch);

        //Sets each one of the text views
        name.setText(fridgeItems[0][position]);
        category.setText(fridgeItems[1][position]);
        qty.setText(fridgeItems[2][position]);
        days.setText(daysIn);
        shelf.setText(fridgeItems[6][position]);

        return v;
    }
    private int daysSinceEpoch()
    {
        //Here we just get the number of milliseconds since UNIX time and divide it by the number
        //below to turn milliseconds into days
        int daysSinceEpoch;
        Date today = new Date();
        daysSinceEpoch = (int)(today.getTime()/86400000);
        return daysSinceEpoch;
    }
}