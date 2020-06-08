package com.example.fridgelog;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class IntroScreen extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introscreen);
        View v = findViewById(R.id.getStartedButton);
        v.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.getStartedButton)
        {
            //Just makes the "fridgelist.txt" file when the user presses the button
            createList();
            Intent mainScreenIntent = new Intent(this, MainScreen.class);
            this.startActivity(mainScreenIntent);
            finish();
            System.exit(0);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //Kills the activity when the back button is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            System.exit(0);
        }
        return true;
    }
    private void createList()
    {
        FileOutputStream fileOutputStream = null;

        try
        {
            fileOutputStream = openFileOutput("fridgelist.txt",MODE_PRIVATE);
            fileOutputStream.write("".getBytes());
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}