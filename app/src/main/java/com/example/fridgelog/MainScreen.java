package com.example.fridgelog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity
{
    private BottomNavigationView nav;
    private FragmentAdapter fragmentAdapter;
    private NoSwipePager viewPager;
    private FridgeListFragment fridgeListFragment;
    private ShoppingListFragment shoppingListFragment;
    private SettingsFragment settingsFragment;
    private FridgeList fridgeList;
    private ShoppingList shoppingList;
    private CategoriesList categoriesList;
    private AutoCompleteItems autoCompleteItems;
    public BackgroundListSaver backgroundListSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        //First we create objects of data structures of the lists so we can use them later
        fridgeList = new FridgeList();
        shoppingList = new ShoppingList();
        categoriesList = new CategoriesList();
        //Then we instantiate a separate thread class for saving the above lists in the background
        backgroundListSaver = new BackgroundListSaver();
        //Then we load the saved files into the list data structures
        loadFridgeList();
        loadShoppingList();
        loadCategoriesList();
        loadAutoCompleteItems();
        //If no items have been added before, the autocompleteitems file will be empty, thereby
        //leaving the data structure as null, so here we check if that's the case
        if (autoCompleteItems == null)
        {
            //so here we just instantiate a new, non-null autocompleteitems object
            autoCompleteItems = new AutoCompleteItems();
        }

        //Here we set up the viewpager in which our fragments are displayed
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        //Here we handle clicks on the bottom navigation bar
        nav = findViewById(R.id.nav);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //For each case we set the viewpager to the fragment corresponding to the item
                //selected in the bottom navigation bar
                //refer to the setCurrentItem and setupViewPager methods for more info
                if (menuItem.getItemId() == R.id.nav_fridge)
                {
                    viewPager.setCurrentItem(0);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_shopping)
                {
                    viewPager.setCurrentItem(1);
                    return true;
                }
                if (menuItem.getItemId() == R.id.nav_settings)
                {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });
    }
    private void setupViewPager(ViewPager viewPager)
    {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

        //Here we instantiate fragments and pass the necessary lists to them using the putParcelable
        //method. This is why all the lists and nodes implement the Parcelable interface.
        //putParcelable is pass by referrence, so any changes we make to the objects in the fragments
        //happen to the objects in this activity
        fridgeListFragment = new FridgeListFragment();
        Bundle fridgeListBundle = new Bundle();
        fridgeListBundle.putParcelable("fridgelist",fridgeList);
        fridgeListBundle.putParcelable("shoppinglist",shoppingList);
        fridgeListBundle.putParcelable("categorieslist",categoriesList);
        fridgeListBundle.putParcelable("autocompleteitems",autoCompleteItems);
        fridgeListFragment.setArguments(fridgeListBundle);

        shoppingListFragment = new ShoppingListFragment();
        Bundle shoppingListBundle = new Bundle();
        shoppingListBundle.putParcelable("shoppinglist",shoppingList);
        shoppingListFragment.setArguments(shoppingListBundle);

        settingsFragment = new SettingsFragment();
        Bundle settingsBundle = new Bundle();
        settingsBundle.putParcelable("categorieslist",categoriesList);
        settingsFragment.setArguments(settingsBundle);

        fragmentAdapter.addFragment(fridgeListFragment);
        fragmentAdapter.addFragment(shoppingListFragment);
        fragmentAdapter.addFragment(settingsFragment);
        viewPager.setAdapter(fragmentAdapter);
    }
    //When the back button is pressed, we save the lists and kill the activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveAndExit();
        }
        return true;
    }
    private void saveAndExit()
    {
        saveListsInBackground();
        finish();
        System.exit(0);
    }
    private void loadFridgeList()
    {
        try
        {
            //Items in the file are saved with the following syntax
            //name*category*quantity*day*month*year*shelf
            FileInputStream fileInputStream = openFileInput("fridgelist.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String fridgeListLine;

            while ( (fridgeListLine = bufferedReader.readLine()) != null)
            {
                //For every line in the file, we first create a new item object
                FridgeItem itemToAdd = new FridgeItem("","",0,0,0,0,0);
                //Then we split the line at asterisks (see syntax above)
                String[] itemLineSplit = fridgeListLine.split("\\*");
                //We set each variable according to the split line array
                itemToAdd.setName(itemLineSplit[0]);
                itemToAdd.setCategory(itemLineSplit[1]);
                itemToAdd.setQuantity(Integer.parseInt(itemLineSplit[2]));
                itemToAdd.setDate(Integer.parseInt(itemLineSplit[3]),
                        Integer.parseInt(itemLineSplit[4]),
                        Integer.parseInt(itemLineSplit[5]));
                itemToAdd.setShelf(Integer.parseInt(itemLineSplit[6]));
                //And we append it to the list
                fridgeList.append(itemToAdd);
            }
            bufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void loadShoppingList()
    {
        try
        {
            //The original plan was to have a "ticked" variable for each shopping list item so that
            //the user can tick them off the list. I ended up abandoning this idea so the splitting
            //here is reduntant.
            //the syntax here is name*ticked (with ticked being 0 or 1)
            FileInputStream fileInputStream = openFileInput("shoppinglist.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String shoppingListLine;

            while ( (shoppingListLine = bufferedReader.readLine()) != null)
            {
                ShoppingListItem itemToAdd = new ShoppingListItem("",(byte)0);
                String[] itemLineSplit = shoppingListLine.split("\\*");
                itemToAdd.setName(itemLineSplit[0]);

                if (itemLineSplit[1].equals("1"))
                {
                    itemToAdd.tick();
                }

                shoppingList.append(itemToAdd);
            }
            bufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void loadCategoriesList()
    {
        try
        {
            //No special syntax here, we just read and append the lines one by one
            FileInputStream fileInputStream = openFileInput("categorieslist.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String categoriesListLine;

            while ( (categoriesListLine = bufferedReader.readLine()) != null)
            {
                categoriesList.append(categoriesListLine);
            }

            bufferedReader.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void loadAutoCompleteItems()
    {
        try
        {
            //Autocomplete items are a hash table array, more info in the AutoCompleteItems class
            //The first line is the size of the array and everything under that are just item names
            FileInputStream fileInputStream = openFileInput("autocompleteitems.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String autoCompleteItemsLine;
            boolean firstLine = true;

            while ( (autoCompleteItemsLine = bufferedReader.readLine()) != null)
            {
                if (firstLine)
                {
                    //If we're on the first line, we create the object with the size given in the
                    //first line and subsequently set firstline to false
                    int size = Integer.parseInt(autoCompleteItemsLine);
                    firstLine = false;
                    autoCompleteItems = new AutoCompleteItems(size);
                }
                else
                {
                    //then we just continue adding the items
                    autoCompleteItems.add(autoCompleteItemsLine);
                }
            }

            bufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //I needed a way of updating the shopping list fragment from the fridge list fragment. Underlying
    //activity methods can be accessed from fragments, so I just made a method here that I call
    //from the fragment.
    public void updateShoppingList()
    {
        shoppingListFragment.updateShoppingList();
    }

    private class BackgroundListSaver extends AsyncTask<Void, Void, Void>
    {
        //I decided to multithread the process of saving the lists to files to prevent potential delays
        //in situations where we save the lists. An AsyncTask keeps a strong reference to its parent
        //activity which can cause a memory link if the AsyncTask stays running for an extended period
        //of time. But in this case, writing the files should realistically take no longer than a
        //few seconds, even for huge numbers of items.
        @Override
        public Void doInBackground(Void... voids)
        {
            //First we make an arraylist containing all the fridge list items so we can iterate
            //through it when saving
            ArrayList<FridgeItem> fridgeItems = fridgeList.toArrayList();

            try
            {
                FileOutputStream fileOutputStream = openFileOutput("fridgelist.txt", Context.MODE_PRIVATE);

                for(FridgeItem item:fridgeItems)
                {
                    //Here we take each variable of each fridge list item, turn them into strings,
                    //combine them into a line according to the syntax, and write them to the file
                    String name = item.getName();
                    String category = item.getCategory();
                    String qty = Integer.toString(item.getQuantity());
                    String day = Integer.toString(item.getDay());
                    String month = Integer.toString(item.getMonth());
                    String year = Integer.toString(item.getYear());
                    String shelf = Integer.toString(item.getShelf());

                    String toWrite = name + "*" + category + "*" + qty + "*" + day + "*" + month + "*" + year + "*" + shelf;
                    fileOutputStream.write(toWrite.getBytes());
                    fileOutputStream.write('\n');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ArrayList<ShoppingListItem> shoppingListItems = shoppingList.toArrayList();

            try
            {
                FileOutputStream fileOutputStream = openFileOutput("shoppinglist.txt", Context.MODE_PRIVATE);

                for(ShoppingListItem item:shoppingListItems)
                {
                    //Similar story as for the fridge list, but with the reduntant syntax containing
                    //the "ticked" variable
                    String name = item.getName();
                    String ticked = Byte.toString(item.isTicked());

                    String toWrite = name + "*" + ticked;
                    fileOutputStream.write(toWrite.getBytes());
                    fileOutputStream.write('\n');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ArrayList<String> categories = categoriesList.toArrayList();

            try
            {
                FileOutputStream fileOutputStream = openFileOutput("categorieslist.txt", Context.MODE_PRIVATE);

                for(String cat:categories)
                {
                    //Simply writes the categories list into the file line by line, no syntax here
                    fileOutputStream.write(cat.getBytes());
                    fileOutputStream.write('\n');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String[] autoCompleteItemsArray = autoCompleteItems.getArray();

            try
            {
                FileOutputStream fileOutputStream = openFileOutput("autocompleteitems.txt", Context.MODE_PRIVATE);

                //Into the first line, we write the size of the hash table array
                int size = autoCompleteItemsArray.length;
                String sizeString = Integer.toString(size);

                fileOutputStream.write(sizeString.getBytes());
                fileOutputStream.write('\n');

                //Then we proceed to write the actual items. We iterate through the array, skipping
                //empty array items and just writing the ones that contain some text
                //Again, more info in the AutoCompleteItems class
                for (int i = 0; i < size; i++)
                {
                    if (!(autoCompleteItemsArray[i].equals("")))
                    {
                        fileOutputStream.write(autoCompleteItemsArray[i].getBytes());
                        fileOutputStream.write('\n');
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Again, we can access this from any child fragment so we use it to save the lists whenever
    //necessary
    public void saveListsInBackground()
    {
        BackgroundListSaver backgroundListSaver = new BackgroundListSaver();
        backgroundListSaver.execute();
    }
}