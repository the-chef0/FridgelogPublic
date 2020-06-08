package com.example.fridgelog;

import android.content.DialogInterface;
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

public class SettingsFragment extends Fragment
{
    private CategoriesList categoriesList;
    private ListView settingsView;
    private int selectedItemIndex;

    public SettingsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Here we retrieve the lists. It's pass by reference so changes we make here
        //actually happen in the MainActivity
        categoriesList = this.getArguments().getParcelable("categorieslist");

        settingsView = view.findViewById(R.id.settingsView);
        //This enables a context menu to pop up when the user clicks an item
        registerForContextMenu(settingsView);
        //Then we populate the the settingsView with the items in the categoriesList using the adapter
        final SettingsAdapter settingsAdapter = new SettingsAdapter(getActivity().getBaseContext(),categoriesList);
        settingsView.setAdapter(settingsAdapter);

        //Here we open a context menu when the user clicks an item
        settingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemIndex = (int) settingsAdapter.getItemId(position);
                getActivity().openContextMenu(settingsView);
            }
        });

        //This handles the add button
        FloatingActionButton addButton = view.findViewById(R.id.addCategoryButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //First we create a new AddCategoryFragment
                AddCategoryFragment addCatDialog = new AddCategoryFragment();
                //Then we ensure that the settingsView updates every time we dismiss the dialog
                //because something has probably been added
                addCatDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateSettingsList();
                    }
                });
                //Then we pass the necessary lists into the add dialog
                Bundle fridgeListBundle = new Bundle();
                fridgeListBundle.putParcelable("categorieslist",categoriesList);
                addCatDialog.setArguments(fridgeListBundle);
                addCatDialog.setTargetFragment(SettingsFragment.this,1);
                //and show the actual dialog
                addCatDialog.show(getFragmentManager(),"");
            }

        });
        return view;
    }
    //Here we handle what happens when the user clicks on an item to
    //open a context menu
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (item.getTitle().equals("Delete category"))
        {
            //In this case we just delete the item at the index and update the list
            categoriesList.delete(selectedItemIndex);
            updateSettingsList();
            return true;
        }
        return false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        //Here we just add stuff to the context menu
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(),0,"Delete category");
    }
    private void updateSettingsList()
    {
        SettingsAdapter newAdapter = new SettingsAdapter(getActivity().getBaseContext(),categoriesList);
        settingsView.setAdapter(newAdapter);
    }
}