/*
The ultimate goal is to somehow keep previously added items around and sugggest them in the
AutoCompleteTextView the next time the user tries to type in a similar item name.
AutoCompleteTextViews need to be populated with arrays, so the logical thing to do was to keep them
in an array. But I wasn't just going to toss everything into an array blindly because that would
produce duplicates if the user enters the same item again. So I decided to make a hash table using
an array where the hash would be a sum of the ASCII values in the string. In this case, a collision
would either mean a duplicate, which this takes care of. Alternatively, it could mean a string
composed of the same letters as something added previously, but in this use case, I think the
probability is low enough to neglect that. I set the default array size to 1024, which takes up like
61k of memory, which I think is pretty acceptable in late 2019.
 */
package com.example.fridgelog;

import android.os.Parcel;
import android.os.Parcelable;

public class AutoCompleteItems implements Parcelable
{
    private String[] array;

    AutoCompleteItems()
    {
        array = new String[1024];

        //We set everything to an empty string because the AutoCompleteTextView adapter doesn't like
        //null objects
        for (int i = 0; i < array.length; i++)
        {
            array[i] = "";
        }
    }
    AutoCompleteItems(int size)
    {
        array = new String[size];

        for (int i = 0; i < array.length; i++)
        {
            array[i] = "";
        }
    }
    //Implementing Parcelable so that we can pass it to the fragments from MainActivity
    protected AutoCompleteItems(Parcel in)
    {
        array = in.createStringArray();
    }
    public static final Creator<AutoCompleteItems> CREATOR = new Creator<AutoCompleteItems>()
    {
        @Override
        public AutoCompleteItems createFromParcel(Parcel in) {
            return new AutoCompleteItems(in);
        }

        @Override
        public AutoCompleteItems[] newArray(int size) {
            return new AutoCompleteItems[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(array);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }
    public void add(String toAdd)
    {
        //First we compute the hash
        int hash = computeHash(toAdd);

        //If it fits into the array, we just add it at the hash index
        if (hash <= array.length-1)
        {
            array[hash] = toAdd;
        }
        else
        {
            //If not, we expand the array to make it fit
            String[] newArray = new String[hash + 1];

            //Copy everything over
            for (int i = 0; i < array.length; i++)
            {
                newArray[i] = array[i];
            }
            //Set everything new to empty strings
            for (int i = array.length; i < newArray.length; i++)
            {
                newArray[i] = "";
            }

            //And add the new item to where it belongs
            newArray[hash] = toAdd;
            array = newArray;
        }
    }
    //This is here so that we can populate the autocomplete suggestions with this array
    public String[] getArray()
    {
        return array;
    }
    private int computeHash(String toHash)
    {
        //Defaults to -1 for if the string is blank
        int hash = -1;

        if (toHash.equals(""))
        {
            return hash;
        }
        else
        {
            //Here it just iterates through the string and cumulatively adds up the ASCII values
            hash = 0;

            for (int i = 0; i < toHash.length(); i++)
            {
                hash += (int)toHash.charAt(i);
            }

            return hash;
        }
    }
}
