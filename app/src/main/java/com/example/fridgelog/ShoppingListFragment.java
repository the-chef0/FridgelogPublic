package com.example.fridgelog;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment
{
    private ShoppingList shoppingList;
    private ListView shoppingListItemsView;
    private FloatingActionButton exportButton;
    private int selectedItemIndex;

    public ShoppingListFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_shoppinglist, container, false);

        //Here we retrieve the lists. It's pass by reference so changes we make here
        //actually happen in the MainActivity
        shoppingList = this.getArguments().getParcelable("shoppinglist");

        shoppingListItemsView = view.findViewById(R.id.shoppingListItemsView);
        //This enables a context menu to pop up when the user clicks an item
        registerForContextMenu(shoppingListItemsView);
        //Then we populate the the settingsView with the items in the categoriesList using the adapter
        final ShoppingListAdapter shoppingListAdapter = new ShoppingListAdapter(getActivity().getBaseContext(),shoppingList);
        shoppingListItemsView.setAdapter(shoppingListAdapter);

        //Here we open a context menu when the user clicks an item
        shoppingListItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemIndex = (int) shoppingListAdapter.getItemId(position);
                getActivity().openContextMenu(shoppingListItemsView);
            }
        });

        //This handles the export button
        exportButton = view.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exportTo();
            }
        });
        return view;
    }
    //Handles context menu
    public void updateShoppingList()
    {
        ShoppingListAdapter newAdapter = new ShoppingListAdapter(getActivity().getBaseContext(),shoppingList);
        shoppingListItemsView.setAdapter(newAdapter);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (item.getTitle().equals("Delete item"))
        {
            shoppingList.delete(selectedItemIndex);
            updateShoppingList();
            return true;
        }
        return false;
    }
    //Here we handle what happens when the user clicks on an item to
    //open a context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        //In this case we just delete the item at the index and update the list
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(),0,"Delete item");
    }
    private void exportTo()
    {
        //We first make sure that everything is saved
        ((MainScreen)getActivity()).saveListsInBackground();
        //Then we get an array list of the shopping list items
        ArrayList<ShoppingListItem> content = shoppingList.toArrayList();
        //and start building up a string with all the items
        StringBuilder stringBuilder = new StringBuilder();

        for (ShoppingListItem item:content)
        {
            //We append the name of the item
            stringBuilder.append(item.getName());
            //and add a new line
            stringBuilder.append('\n');
        }

        //Create the actual string
        String message = stringBuilder.toString();

        //And pass it into the built-in Android share mechanism
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose destination"));
    }
}