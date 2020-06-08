/*
Here I just defined classes for the FridgeItem and the ShoppingListItem by inheritance
from a base Item class. When I originally wrote this, I thought the inheritance and polymorphism
might come in handy, for example when turning FridgeItems into ShoppingListItems, but it
turned out not to be the case. Anyway, I don't think explanations are needed here. Just classes,
private class variables, accessors and mutators for those variables, and of course the Parcelable
interface so that we can pass them around between activities and fragments.
 */
package com.example.fridgelog;

import android.os.Parcel;
import android.os.Parcelable;

public class Item
{
    private String name;

    Item() {}
    Item(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}

class FridgeItem extends Item implements Parcelable
{
    private String category;
    private int quantity;
    private int day;
    private int month;
    private int year;
    private int shelf;

    FridgeItem(String name, String category, int quantity, int day, int month, int year, int shelf)
    {
        super(name);
        this.category = category;
        this.quantity = quantity;
        this.day = day;
        this.month = month;
        this.year = year;
        this.shelf = shelf;
    }
    protected FridgeItem(Parcel in)
    {
        category = in.readString();
        quantity = in.readInt();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
        shelf = in.readInt();
    }
    public static final Creator<FridgeItem> CREATOR = new Creator<FridgeItem>()
    {
        @Override
        public FridgeItem createFromParcel(Parcel in) {
            return new FridgeItem(in);
        }

        @Override
        public FridgeItem[] newArray(int size) {
            return new FridgeItem[size];
        }
    };
    public String getCategory()
    {
        return this.category;
    }
    public int getQuantity()
    {
        return this.quantity;
    }
    public int getDay()
    {
        return this.day;
    }
    public int getMonth()
    {
        return this.month;
    }
    public int getYear()
    {
        return this.year;
    }
    public int getShelf()
    {
        return this.shelf;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }
    public void setQuantity(int quantity)
    {
        if (quantity < 1)
        {
            throw new IllegalArgumentException();
        }
        this.quantity = quantity;
    }
    public void setDate(int day, int month, int year)
    {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    public void setShelf(int shelf)
    {
        this.shelf = shelf;
    }
    @Override
    public int describeContents()
    {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(category);
        dest.writeInt(quantity);
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
        dest.writeInt(shelf);
    }
}

class ShoppingListItem extends Item implements Parcelable
{
    private byte ticked;

    ShoppingListItem(String name, byte ticked)
    {
        super(name);
        this.ticked = ticked;
    }
    public static final Creator<ShoppingListItem> CREATOR = new Creator<ShoppingListItem>()
    {
        @Override
        public ShoppingListItem createFromParcel(Parcel in) {
            return new ShoppingListItem(in);
        }

        @Override
        public ShoppingListItem[] newArray(int size) {
            return new ShoppingListItem[size];
        }
    };
    public ShoppingListItem(Parcel in)
    {
        ticked = in.readByte();
    }
    public byte isTicked()
    {
        return this.ticked;
    }
    public void tick()
    {
        this.ticked = 1;
    }
    public void untick()
    {
        this.ticked = 0;
    }
    @Override
    public int describeContents()
    {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte(ticked);
    }
}