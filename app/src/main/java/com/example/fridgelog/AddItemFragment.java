package com.example.fridgelog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AddItemFragment extends DialogFragment
{
    private FridgeList fridgeList;
    private CategoriesList categoriesList;
    private AutoCompleteItems autoCompleteItems;
    private AutoCompleteTextView addName;
    private ImageView scanButton;
    private TextView addDate;
    private long defaultDate;
    private int[] selectedDate;
    final private Calendar c = Calendar.getInstance();
    private Spinner catSelector;
    private EditText addQuantity;
    private EditText addShelf;
    private Button addButton;
    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener)
    {
        this.onDismissListener = onDismissListener;
    }
    //Has to be overridden, nothing to explain here
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
    @Override
    public void onStart()
    {
        //We need to manually set the dimensions because it sucks automatically
        super.onStart();
        getDialog().getWindow().setLayout(1050,800);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.additempopup,container,false);

        //Here we retrieve the lists. It's pass by reference so changes we make here
        //actually happen in the MainActivity
        fridgeList = this.getArguments().getParcelable("fridgelist");
        categoriesList = this.getArguments().getParcelable("categorieslist");
        autoCompleteItems = this.getArguments().getParcelable("autocompleteitems");

        addName = view.findViewById(R.id.add_name);
        //Here we populate the autofill options with the hash table array of previously added items
        //More info in the AutoCompleteItems class
        ArrayAdapter<String> autofillAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, autoCompleteItems.getArray());
        addName.setAdapter(autofillAdapter);

        //This part handles the barcode scanner button
        scanButton = view.findViewById(R.id.add_scanbutton);
        scanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //We check if there;s an internet connection cuz it's necessary for the barcode
                //scanner
                ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(CONNECTIVITY_SERVICE);

                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                    //If there is a connection, we start the BarcodeScanner activity
                    //Here we need to use the startActivityForResult method, not the usual startActivity
                    //method because we need to get the detected product name back
                    Intent barcodeIntent = new Intent(getActivity().getBaseContext(),BarcodeScanner.class);
                    //I arbitrarily picked "1" as a request code to retrieve the results later
                    //Not the best practice, I know. I don't feel like fucking with it anymore.
                    //Feel free to improve this with a static final variable if you're someone who
                    // //is not me and working on this for whatever reason.
                    startActivityForResult(barcodeIntent,1);
                }
                else
                {
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        addDate = view.findViewById(R.id.add_date);
        //The date defaults to the current date. To do this we first get the current time in milliseconds
        defaultDate = System.currentTimeMillis();
        //Then we turn it into a string with this format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(defaultDate);
        addDate.setText(dateString);
        //If we click on it to change the date, it pulls up a date picker dialog fragment
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We pull up the date picker fragment
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AppCompatDialogFragment datePickerFragment = new DatePickerFragment();
                //I arbitrarily picked "11" as a request code to retrieve the results later
                datePickerFragment.setTargetFragment(AddItemFragment.this,11);
                datePickerFragment.show(fm,"");
            }
        });

        catSelector = view.findViewById(R.id.add_categories);
        //Here we populate the category selector spinner with an array generated by the
        //categoriesList object. More info in the corresponding class.
        final ArrayAdapter<String> catSelectorAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1,
                categoriesList.toArray());
        catSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSelector.setAdapter(catSelectorAdapter);

        //We set the quantity and shelf number to 1 as the default
        addQuantity = view.findViewById(R.id.add_qty);
        addQuantity.setText("1");

        addShelf = view.findViewById(R.id.add_shelf);
        addShelf.setText("1");

        //Time to handle the add button
        addButton = view.findViewById(R.id.add_savebutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //First we try to get the user inputted name. If it's not empty, we proceed
                String name = addName.getText().toString();
                if (!name.equals(""))
                {
                    //We create a category string and default it to "No category"
                    String category = "No category";

                    //If the user selects something from the spinner, it gets replaced with that
                    if (!(catSelector.getSelectedItem() == null))
                    {
                        category = catSelector.getSelectedItem().toString();
                    }
                    //Here we create and set the calendar variables to whatever the calendar object
                    //is set to
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    //And parse the text from the quantity and shelf editTexts into integers
                    int quantity = Integer.parseInt(addQuantity.getText().toString());
                    int shelf = Integer.parseInt(addShelf.getText().toString());

                    //Then we greate a new FridgeItem, set everything that we just made and append
                    //it to the list
                    FridgeItem toAdd = new FridgeItem(name,category,quantity,day,month,year,shelf);
                    fridgeList.append(toAdd);
                    //We also add it to the list of previously added items for autocomplete
                    autoCompleteItems.add(name);
                    //and dismiss it
                    getDialog().dismiss();
                } else
                {
                    //If it is empty, we inform the user that they are, indeed, retarded
                    Toast.makeText(getActivity().getBaseContext(), "Name not selected.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    //This part handles results sent from the calendar dialog fragment and barcode scanner
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Request code 11 is the calendar dialog fragment
        if (requestCode == 11 && resultCode == Activity.RESULT_OK)
        {
            //First we retrieve an array containing the user-selected day, month and year
            selectedDate = data.getIntArrayExtra("selectedDate");
            //And set the new values in the calendar object
            c.set(Calendar.DAY_OF_MONTH,selectedDate[0]);
            c.set(Calendar.MONTH,selectedDate[1]);
            c.set(Calendar.YEAR,selectedDate[2]);
            //Then flip that shit into our lovely european date format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            long selectedDateMs = c.getTimeInMillis();
            String selectedDateString = sdf.format(selectedDateMs);
            //and display it in the TextView
            addDate.setText(selectedDateString);
        }
        //Request code 1 is the barcode scanner
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            //First we try to retrieve the product name
            //More info in the BarcodeScanner class
            String scanResult = data.getStringExtra("result");

            //If the barcode scanner fails to get the product name, it sends back an empty string
            if (scanResult.equals(""))
            {
                Toast.makeText(getActivity().getBaseContext(),"Product not found.", Toast.LENGTH_LONG).show();
            }
            addName.setText(scanResult);
        }
    }
}