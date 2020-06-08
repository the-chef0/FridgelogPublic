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

class FNode implements Parcelable
{
    FridgeItem item;
    FNode prev;
    FNode next;

    FNode() {}

    //Implements Parcelable so that we can pass it from MainActivity into fragments
    protected FNode(Parcel in)
    {
        item = in.readParcelable(FridgeItem.class.getClassLoader());
        prev = in.readParcelable(FNode.class.getClassLoader());
        next = in.readParcelable(FNode.class.getClassLoader());
    }
    public static final Creator<FNode> CREATOR = new Creator<FNode>()
    {
        @Override
        public FNode createFromParcel(Parcel in) {
            return new FNode(in);
        }

        @Override
        public FNode[] newArray(int size) {
            return new FNode[size];
        }
    };
    @Override
    public int describeContents()
    {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(item, flags);
        dest.writeParcelable(prev, flags);
        dest.writeParcelable(next, flags);
    }
}

public class FridgeList implements Parcelable
{
    private FNode head;
    private FNode tail;
    //I'm keeping a size variable here so I don't have to traverse the whole thing when finding
    //the size. This is useful in the toArrays method.
    private int size;

    FridgeList()
    {
        this.head = null;
        this.tail = this.head;
        size = 0;
    }
    /*
    Essential public methods for in-program functionality
     */
    protected FridgeList(Parcel in)
    {
        head = in.readParcelable(FNode.class.getClassLoader());
        tail = in.readParcelable(FNode.class.getClassLoader());
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
    public static final Creator<FridgeList> CREATOR = new Creator<FridgeList>()
    {
        @Override
        public FridgeList createFromParcel(Parcel in) {
            return new FridgeList(in);
        }

        @Override
        public FridgeList[] newArray(int size) {
            return new FridgeList[size];
        }
    };
    private int size()
    {
        return this.size;
    }
    public FridgeItem get(int index)
    {
        //Handling edge cases first
        if (this.head == null)
        {
            throw new NullPointerException();
        }

        if (index < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        //If the index is 0, we just return the head
        if (index == 0)
        {
            return this.head.item;
        }
        else
        {
            int counter = 1;
            FNode curr = this.head.next;

            //If none of the edge cases are true, we iterate through the list until we get to the
            //index we want
            while (curr != null)
            {
                if (counter == index)
                {
                    return curr.item;
                }
                else
                {
                    counter++;
                    curr = curr.next;
                }
            }
        }
        //reached only if whole list gets iterated through without finding index
        throw new IndexOutOfBoundsException();
    }
    public void append(FridgeItem item)
    {
        FNode newFNode = new FNode();
        newFNode.item = item;

        //If the list is empty, we make a make a new head
        if (this.head == null)
        {
            this.head = newFNode;
            this.tail = this.head;
        }
        else
        //If not, we point the tail to it
        {
            this.tail.next = newFNode;
            newFNode.prev = this.tail;
            this.tail = newFNode;
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
            FNode curr = this.head.next;

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
    public String[][] toArrays()
    {
        //This is why we're keeping the size variable, so that we don't have to iterate through
        //the whole thing to find it
        //We're making a 2D array so that we can store all 8 variables for each item
        String[][] arrays = new String[7][this.size()];
        FNode curr = this.head;
        int i = 0;

        //Iterate through the whole thing and put the variables into the array
        while (curr != null)
        {
            arrays[0][i] = curr.item.getName();
            arrays[1][i] = curr.item.getCategory();
            arrays[2][i] = String.valueOf(curr.item.getQuantity());
            arrays[3][i] = String.valueOf(curr.item.getDay());
            arrays[4][i] = String.valueOf(curr.item.getMonth());
            arrays[5][i] = String.valueOf(curr.item.getYear());
            arrays[6][i] = String.valueOf(curr.item.getShelf());
            i++;
            curr = curr.next;
        }
        return arrays;
    }
    public ArrayList<FridgeItem> toArrayList()
    {
        //Just traverses the whole thing and adds it into an arraylist
        ArrayList<FridgeItem> fridgeItems = new ArrayList<>();
        FNode curr = this.head;

        while(curr != null)
        {
            fridgeItems.add(curr.item);
            curr = curr.next;
        }
        return fridgeItems;
    }
    /*
    Methods of and related to the sorting algorithm
     */
    public void sort(int sortKey) //sortKey: 0 - name, 1 - category, 2 - shelf
    {
        //Edge cases first
        if (this.head == null)
        {
            throw new NullPointerException();
        }

        if (this.hasSizeOne())
        {
            return;
        }
        else
        {
            //First we make two new lists to split the list into left and right
            FridgeList left = new FridgeList();
            FridgeList right = new FridgeList();

            //Then we find the index of the half point. Here, keeping track of the size in a variable
            //comes in handy again
            int halfIndex = (this.size()-1)/2;
            //Then we make a pointer to the node at the half point
            FNode halfFNode = this.getFNode(halfIndex);
            //Then move the pointers around so that we divide it into left and right lists
            left.head = this.head;
            left.tail = halfFNode;
            right.head = halfFNode.next;
            right.tail = this.tail;
            right.head.prev = null;
            left.tail.next = null;

            //Recursive calls
            left.sort(sortKey);
            right.sort(sortKey);

            //Here we decide whether to sort it by name, category or shelf number
            if (sortKey == 0)
            {
                this.mergeByName(left,right);
            }
            else if (sortKey == 1)
            {
                this.mergeByCategory(left,right);
            }
            else if (sortKey == 2)
            {
                this.mergeByShelf(left,right);
            }
            else throw new IllegalArgumentException();
        }
    }
    private boolean hasSizeOne()
    {
        return (this.head == this.tail && head != null);
    }
    private void mergeByName(FridgeList left, FridgeList right)
    {
        //We make pointers to the start of the left and right lists
        FNode leftPointer = left.head;
        FNode rightPointer = right.head;
        //and a new list to merge into
        FridgeList merged = new FridgeList();

        //This loop keeps going until both left and right are exhausted
        while (!(leftPointer == null && rightPointer == null))
        {
            //If the left list is exhausted
            if (leftPointer == null)
            {
                //we keep adding items from the right list until we exhaust that one as well
                while (rightPointer != null)
                {
                    merged.append(rightPointer.item);
                    rightPointer = rightPointer.next;
                }
                break;
            }

            //if the right list is exhausted
            if (rightPointer == null)
            {
                //we keep adding items from the left list until we exhaust that one as well
                while (leftPointer != null)
                {
                    merged.append(leftPointer.item);
                    leftPointer = leftPointer.next;
                }
                break;
            }

            //If the first letter of the left pointer comes before the right one in the alphabet
            if (leftPointer.item.getName().codePointAt(0) < rightPointer.item.getName().codePointAt(0))
            {
                merged.append(leftPointer.item);
                leftPointer = leftPointer.next;
                continue;
            }

            //If the first letter of the right pointer comes before the left one in the alphabet
            if (rightPointer.item.getName().codePointAt(0) < leftPointer.item.getName().codePointAt(0))
            {
                merged.append(rightPointer.item);
                rightPointer = rightPointer.next;
                continue;
            }

            //In case the first letter is the same
            if (leftPointer.item.getName().codePointAt(0) == rightPointer.item.getName().codePointAt(0))
            {
                //We put the names into strings so we don't have to type out the whole getName thing
                String leftName = leftPointer.item.getName();
                String rightName = rightPointer.item.getName();

                //If the left name is shorter, we compare characters up to the end of the left name
                if (leftName.length() < rightName.length())
                {
                    //Assume left is a substring of right
                    boolean found = false;

                    //Then we compare characters until we find one that differs
                    for (int index = 1; index < leftName.length(); index++)
                    {
                        //If the left character is smaller than the right character
                        if (leftName.codePointAt(index) < rightName.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            found = true;
                            break;
                        }

                        //If the right character is smaller than the left character
                        if (rightName.codePointAt(index) < rightName.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            found = true;
                            break;
                        }
                    }

                    //This if statement might be redundant now that I think about it
                    if (found)
                    {
                        continue;
                    }
                    else
                    {
                        //If this part gets reached it means that no characters differ, i.e. left
                        //is a substring of right, so we add left into the merged list
                        merged.append(leftPointer.item);
                        leftPointer = leftPointer.next;
                    }
                }

                //If the right name is shorter, we compare characters up to the end of the right name
                if (rightName.length() < leftName.length())
                {
                    //Assume right is a substring of left
                    boolean found = false;

                    //Then we compare characters until we find one that differs
                    for (int index = 1; index < rightName.length(); index++)
                    {
                        //If the left character is smaller than the right character
                        if (leftName.codePointAt(index) < rightName.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            found = true;
                            break;
                        }

                        //If the right character is smaller than the left character
                        if (rightName.codePointAt(index) < leftName.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            found = true;
                            break;
                        }
                    }

                    //This if statement might be redundant now that I think about it
                    if (found)
                    {
                        continue;
                    }
                    else
                    {
                        //If this part gets reached it means that no characters differ, i.e. right
                        //is a substring of left, so we add right into the merged list
                        merged.append(rightPointer.item);
                        rightPointer = rightPointer.next;
                    }
                }

                //Equal length which might mean same name
                if (leftName.length() == rightName.length())
                {
                    //We assume that they are the same
                    boolean differenceFound = false;

                    //They are the same length, so I arbitrarily picked the length of the left to be
                    //the bounday of the loop
                    for (int index = 0; index < leftName.length(); index++)
                    {
                        //Then keep comparing the characters until we find a difference
                        if (leftName.codePointAt(index) < rightName.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            differenceFound = true;
                            break;
                        }

                        if (rightName.codePointAt(index) < leftName.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            differenceFound = true;
                            break;
                        }
                    }

                    if (differenceFound)
                    {
                        continue;
                    }
                    else
                    {
                        //Reached if names are the same, selects the left one
                        merged.append(leftPointer.item);
                        leftPointer = leftPointer.next;
                    }
                }
            }
        }
        this.head = merged.head;
        this.tail = merged.tail;
    }
    private void mergeByCategory(FridgeList left, FridgeList right)
    {
        //The concept here is the same as in mergeByName, except we compare categories
        FNode leftPointer = left.head;
        FNode rightPointer = right.head;
        FridgeList merged = new FridgeList();

        while (!(leftPointer == null && rightPointer == null))
        {
            if (leftPointer == null)
            {
                while (rightPointer != null)
                {
                    merged.append(rightPointer.item);
                    rightPointer = rightPointer.next;
                }
                break;
            }

            if (rightPointer == null)
            {
                while (leftPointer != null)
                {
                    merged.append(leftPointer.item);
                    leftPointer = leftPointer.next;
                }
                break;
            }

            if (leftPointer.item.getCategory().codePointAt(0) < rightPointer.item.getCategory().codePointAt(0))
            {
                merged.append(leftPointer.item);
                leftPointer = leftPointer.next;
                continue;
            }

            if (rightPointer.item.getCategory().codePointAt(0) < leftPointer.item.getCategory().codePointAt(0))
            {
                merged.append(rightPointer.item);
                rightPointer = rightPointer.next;
                continue;
            }

            //In case the first letter is similar:
            if (leftPointer.item.getCategory().codePointAt(0) == rightPointer.item.getCategory().codePointAt(0))
            {
                String leftString = leftPointer.item.getCategory();
                String rightString = rightPointer.item.getCategory();

                //Left category shorter
                if (leftString.length() < rightString.length())
                {
                    boolean found = false;

                    for (int index = 1; index < leftString.length(); index++)
                    {
                        if (leftString.codePointAt(index) < rightString.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            found = true;
                            break;
                        }

                        if (rightString.codePointAt(index) < leftString.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            found = true;
                            break;
                        }
                    }

                    if (found)
                    {
                        continue;
                    }
                    else
                    {
                        merged.append(leftPointer.item);
                        leftPointer = leftPointer.next;
                    }
                }

                //Right category shorter
                if (rightString.length() < leftString.length())
                {
                    boolean found = false;

                    for (int index = 1; index < rightString.length(); index++)
                    {
                        if (leftString.codePointAt(index) < rightString.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            found = true;
                            break;
                        }

                        if (rightString.codePointAt(index) < leftString.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            found = true;
                            break;
                        }
                    }

                    if (found)
                    {
                        continue;
                    }
                    else
                    {
                        merged.append(rightPointer.item);
                        rightPointer = rightPointer.next;
                    }

                }

                //Equal length which might mean same category
                if (leftString.length() == rightString.length())
                {
                    //To find out if the categories are the same
                    boolean differenceFound = false;

                    for (int index = 0; index < leftString.length(); index++)
                    {
                        if (leftString.codePointAt(index) < rightString.codePointAt(index))
                        {
                            merged.append(leftPointer.item);
                            leftPointer = leftPointer.next;
                            differenceFound = true;
                            break;
                        }

                        if (rightString.codePointAt(index) < leftString.codePointAt(index))
                        {
                            merged.append(rightPointer.item);
                            rightPointer = rightPointer.next;
                            differenceFound = true;
                            break;
                        }
                    }

                    if (differenceFound)
                    {
                        continue;
                    }
                    else
                    {
                        //Reached if categories are the same, selects the left one
                        merged.append(leftPointer.item);
                        leftPointer = leftPointer.next;
                    }
                }
            }
        }
        this.head = merged.head;
        this.tail = merged.tail;
    }
    private void mergeByShelf(FridgeList left, FridgeList right)
    {
        //This one is much simpler than mergeByName and mergeByCategory because we're just comparing
        //numbers. I'm not going to bother explaining classic merge sort here.
        FNode leftPointer = left.head;
        FNode rightPointer = right.head;
        FridgeList merged = new FridgeList();

        while (!(leftPointer == null && rightPointer == null))
        {
            if (leftPointer == null)
            {
                while (rightPointer != null)
                {
                    merged.append(rightPointer.item);
                    rightPointer = rightPointer.next;
                }
                break;
            }

            if (rightPointer == null)
            {
                while (leftPointer != null)
                {
                    merged.append(leftPointer.item);
                    leftPointer = leftPointer.next;
                }
                break;
            }

            if (leftPointer.item.getShelf() < rightPointer.item.getShelf())
            {
                merged.append(leftPointer.item);
                leftPointer = leftPointer.next;
                continue;
            }

            if (rightPointer.item.getShelf() < leftPointer.item.getShelf())
            {
                merged.append(rightPointer.item);
                rightPointer = rightPointer.next;
                continue;
            }

            if (rightPointer.item.getShelf() == leftPointer.item.getShelf())
            {
                merged.append(leftPointer.item);
                leftPointer = leftPointer.next;
                continue;
            }
        }
        this.head = merged.head;
        this.tail = merged.tail;
    }
    /*
    Private methods for internal functionality
     */
    private FNode getFNode(int index)
    {
        //Same idea as the get method, except it takes the whole node object
        if (index < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0)
        {
            return this.head;
        }
        else
        {
            int counter = 1;
            FNode curr = this.head.next;

            while (curr != null)
            {
                if (counter == index)
                {
                    return curr;
                }
                else
                {
                    counter++;
                    curr = curr.next;
                }
            }
        }
        //reached only if whole list gets iterated through without finding index
        throw new IndexOutOfBoundsException();
    }
}