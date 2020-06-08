/*
All lists - fridge, shopping and categories - are linked lists. This is because a) append operations
are O(1) and the user will be doing a lot of those, b) Delete operations are O(n) in the worst case
which is in most cases faster than other data structures that could be used here, and moving
pointers around is very simple, c)Sorting with mergesort is O(n(logn)) which is fast as fuck. The
only problem is that accessing the individual nodes is O(n) in the worst case, but that's still
not tragic. I mean, how much shit can one have in a fridge?
Also, I made it a doubly linked list because I thought it would be useful, but it turns out to be
reduntant in the end. Single link would suffice... I think. I could be wrong, I haven't fully
analyzed it.
 */
package com.example.fridgelog;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

class CNode implements Parcelable
{
    String cat;
    CNode prev;
    CNode next;

    CNode() {}

    //Implements Parcelable so that we can pass it from MainActivity into fragments
    protected CNode(Parcel in)
    {
        cat = in.readString();
        prev = in.readParcelable(CNode.class.getClassLoader());
        next = in.readParcelable(CNode.class.getClassLoader());
    }
    public static final Parcelable.Creator<CNode> CREATOR = new Parcelable.Creator<CNode>()
    {
        @Override
        public CNode createFromParcel(Parcel in)
        {
            return new CNode(in);
        }
        @Override
        public CNode[] newArray(int size)
        {
            return new CNode[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cat);
        dest.writeParcelable(prev, flags);
        dest.writeParcelable(next, flags);
    }
}

public class CategoriesList implements Parcelable
{
    private CNode head;
    private CNode tail;
    //I'm keeping a size variable here so I don't have to traverse the whole thing when finding
    //the size. This is useful in the toArray method.
    private int size;

    CategoriesList()
    {
        this.head = null;
        this.tail = this.head;
        this.size = 0;
    }
    /*
    Essential public methods for in-program functionality
     */
    protected CategoriesList(Parcel in)
    {
        head = in.readParcelable(CNode.class.getClassLoader());
        tail = in.readParcelable(CNode.class.getClassLoader());
        size = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(head, flags);
        dest.writeParcelable(tail, flags);
        dest.writeInt(size);
    }
    @Override
    public int describeContents()
    {
        return 0;
    }
    public static final Parcelable.Creator<FridgeList> CREATOR = new Parcelable.Creator<FridgeList>()
    {
        @Override
        public FridgeList createFromParcel(Parcel in)
        {
            return new FridgeList(in);
        }
        @Override
        public FridgeList[] newArray(int size)
        {
            return new FridgeList[size];
        }
    };
    private int size()
    {
        return this.size;

    }
    public void append(String cat) {
        CNode newNode = new CNode();
        newNode.cat = cat;

        //If the list is empty, we make a make a new head
        if (this.head == null) {
            this.head = newNode;
            this.tail = this.head;
        } else
            //If not, we point the tail to it
        {
            this.tail.next = newNode;
            newNode.prev = this.tail;
            this.tail = newNode;
        }

        //And of course increase the size
        this.size++;
    }
    public void delete(int index)
    {
        //Edge cases which i don't think are even reachable through the UI, but just in case
        if (this.head == null)
        {
            throw new NullPointerException();
        }

        if (index < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        //Keeping track of if we found the index so we can potentially throw an exception
        boolean foundAndDeleted = false;

        if (index == 0)
        {
            //deleting the head
            this.head = this.head.next;
            foundAndDeleted = true;
        }
        else
        {
            //deleting anything that isn't the head or tail
            int counter = 1;
            CNode curr = this.head.next;

            //Traverse the list until we get to our index
            while (curr.next != null)
            {
                if (counter == index)
                {
                    //If we reach the index, we switch the pointers around
                    curr.prev.next = curr.next;
                    curr.next.prev = curr.prev;
                    foundAndDeleted = true;
                    break;
                }
                else
                {
                    //If not we keep going
                    curr = curr.next;
                    counter++;
                }
            }

            if (curr.next == null && counter == index)
            {
                //Here we reached the tail and are trying to delete it
                this.tail = this.tail.prev;
                this.tail.next = null;
                foundAndDeleted = true;
            }
        }

        if (!foundAndDeleted)
        {
            //If we haven't found it, the index must be out of bounds
            throw new IndexOutOfBoundsException();
        }
        else
        {
            //If we found it, decrease the size
            this.size--;
        }
    }
    public String[] toArray()
    {
        //This is why we're keeping the size variable, so that we don't have to iterate through
        //the whole thing to find it
        String[] array = new String[this.size()];
        CNode curr = this.head;
        int i = 0;

        //We just traverse the list and add everything into the array
        while (curr != null)
        {
            array[i] = curr.cat;
            i++;
            curr = curr.next;
        }
        return array;
    }
    public ArrayList<String> toArrayList()
    {
        //Just traverses the whole thing and adds it into an arraylist
        ArrayList<String> categories = new ArrayList<>();
        CNode curr = this.head;

        while(curr != null)
        {
            categories.add(curr.cat);
            curr = curr.next;
        }
        return categories;
    }
}