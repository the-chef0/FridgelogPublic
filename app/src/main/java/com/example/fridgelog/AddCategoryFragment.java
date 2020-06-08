package com.example.fridgelog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddCategoryFragment extends DialogFragment
{
    private CategoriesList categoriesList;
    private EditText catName;
    private Button addCategoryButton;
    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener)
    {
        this.onDismissListener = onDismissListener;
    }
    //Nothing to explain here, it has to be overridden if I remember correctly
    @Override
    public void onDismiss(DialogInterface dialog)
    {
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
        getDialog().getWindow().setLayout(860,550);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.addcategorypopup,container,false);
        //Here we retrieve the categories list. It's pass by reference so changes we make here
        //actually happen in the MainActivity
        categoriesList = this.getArguments().getParcelable("categorieslist");

        catName = view.findViewById(R.id.cat_name);
        addCategoryButton = view.findViewById(R.id.cat_savebutton);

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!catName.getText().toString().equals(""))
            {
                categoriesList.append(catName.getText().toString());
            }
            //If the user doesn't input anything, we just dismiss the dialog
            getDialog().dismiss();
            }
        });
        return view;
    }
}