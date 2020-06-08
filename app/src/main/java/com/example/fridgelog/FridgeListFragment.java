package com.example.fridgelog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FridgeListFragment extends Fragment
{
    private FridgeList fridgeList;
    private ShoppingList shoppingList;
    private CategoriesList categoriesList;
    private AutoCompleteItems autoCompleteItems;
    private ListView fridgeItemsView;
    private int selectedItemIndex;

    public FridgeListFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fridgelist, container, false);

        //Here we retrieve the lists. It's pass by reference so changes we make here
        //actually happen in the MainActivity
        fridgeList = this.getArguments().getParcelable("fridgelist");
        shoppingList = this.getArguments().getParcelable("shoppinglist");
        categoriesList = this.getArguments().getParcelable("categorieslist");
        autoCompleteItems = this.getArguments().getParcelable("autocompleteitems");

        fridgeItemsView = view.findViewById(R.id.fridgeItemsView);
        //This enables a context menu to pop up when the user clicks an item
        registerForContextMenu(fridgeItemsView);
        //Then we populate the the fridgeItemsView with the items in the fridge list using the adapter
        final FridgeListAdapter fridgeListAdapter = new FridgeListAdapter(getActivity().getBaseContext(),fridgeList);
        fridgeItemsView.setAdapter(fridgeListAdapter);

        //Here we open a context menu when the user clicks an item
        fridgeItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemIndex = (int) fridgeListAdapter.getItemId(position);
                getActivity().openContextMenu(fridgeItemsView);
            }
        });

        //This handles the add button
        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //First we create a new AddItemFragment
                AddItemFragment addItemDialog = new AddItemFragment();
                //Then we ensure that the fridgeItemsView updates every time we dismiss the dialog
                //because something has probably been added
                addItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateFridgeList();
                    }
                });
                //Then we pass the necessary lists into the add dialog
                Bundle fridgeListBundle = new Bundle();
                fridgeListBundle.putParcelable("fridgelist",fridgeList);
                fridgeListBundle.putParcelable("categorieslist",categoriesList);
                fridgeListBundle.putParcelable("autocompleteitems",autoCompleteItems);
                addItemDialog.setArguments(fridgeListBundle);
                addItemDialog.setTargetFragment(FridgeListFragment.this,1);
                //and show the actual dialog
                addItemDialog.show(getFragmentManager(),"");
            }

        });

        //Here we create and populate the spinner with the sorting options from a pre-defined array
        //in strings.xml
        Spinner sortSelector = view.findViewById(R.id.sortSelector);
        ArrayAdapter<String> sortSelectorAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sortingOptions));
        sortSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSelector.setAdapter(sortSelectorAdapter);

        sortSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            //Here we check which one of the sorting options was chosen and sort the fridge
            //list accordingly
            //More info about the sort method in the FridgeList class
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position).equals("Name"))
                {
                    fridgeList.sort(0);
                    updateFridgeList();
                }

                if (parent.getItemAtPosition(position).equals("Category"))
                {
                    fridgeList.sort(1);
                    updateFridgeList();
                }

                if (parent.getItemAtPosition(position).equals("Shelf"))
                {
                    fridgeList.sort(2);
                    updateFridgeList();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return view;
    }
    //Here we handle what happens when the user clicks on an item to
    //open a context menu
    //These if statements could probably be replaced with switch cases
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getTitle().equals("Edit item"))
        {
            //In this case, we first create a new EditItemFragment
            EditItemFragment editItemDialog = new EditItemFragment();
            //Make sure the fridge list updates when we dismiss the dialog after
            //editing an item
            editItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    updateFridgeList();
                }
            });
            //Then pass the necessary stuff into the dialog
            Bundle fridgeListBundle = new Bundle();
            fridgeListBundle.putParcelable("fridgelist",fridgeList);
            fridgeListBundle.putInt("index",selectedItemIndex);
            fridgeListBundle.putParcelable("categorieslist",categoriesList);
            fridgeListBundle.putParcelable("autocompleteitems",autoCompleteItems);
            editItemDialog.setArguments(fridgeListBundle);
            editItemDialog.setTargetFragment(FridgeListFragment.this,1);
            //And show it
            editItemDialog.show(getFragmentManager(),"");
            return true;
        }

        if (item.getTitle().equals("Add to shopping list"))
        {

            //Here we create a new ShoppingListItem with the name of the FridgeItem at the selected index
            ShoppingListItem toAdd = new ShoppingListItem(fridgeList.get(selectedItemIndex).getName(),(byte)0);
            //Add it to the shoppingList
            shoppingList.append(toAdd);
            //And update the shopping list
            //This is why I made the public method in the MainActivity
            ((MainScreen)getActivity()).updateShoppingList();
            Toast.makeText(getActivity().getBaseContext(),"Added " + toAdd.getName() + " to shopping list.",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (item.getTitle().equals("Delete"))
        {
            //Just deletes the item at the index and updates the FridgeList
            fridgeList.delete(selectedItemIndex);
            updateFridgeList();
            return true;
        }
        return false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        //Here we just add stuff to the context menu
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(),0,"Edit item");
        menu.add(0,v.getId(),0,"Add to shopping list");
        menu.add(0,v.getId(),0,"Delete");
    }

    private void updateFridgeList()
    {
        FridgeListAdapter newAdapter = new FridgeListAdapter(getActivity().getBaseContext(),fridgeList);
        fridgeItemsView.setAdapter(newAdapter);
    }
}