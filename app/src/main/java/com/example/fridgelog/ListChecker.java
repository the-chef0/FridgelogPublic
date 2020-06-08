/*
This class checks for the presence of a file named "fridgelist.txt" in the app data directory,
thereby checking whether the app is being opened for the first time. If it doesn't exist, it pulls
up the intro screen where the user can set up the app for the first time.
 */
package com.example.fridgelog;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ListChecker extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        File potentialFridgeList = getBaseContext().getFileStreamPath("fridgelist.txt");
        if (potentialFridgeList.exists())
        {
            //If the file exists, we open the main screen of the app
            Intent mainScreenIntent = new Intent(this, MainScreen.class);
            this.startActivity(mainScreenIntent);
            //Kills this activity so that the user can't come back to it with the back button
            finish();
            System.exit(0);
        }
        else
        {
            //If it doesn't exist, we open the intro screen that lets the user create the file
            Intent mainScreenIntent = new Intent(this, IntroScreen.class);
            this.startActivity(mainScreenIntent);
            //Again, we kill it to prevent back button issues
            finish();
            System.exit(0);
        }
    }
}