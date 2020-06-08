/*
The largest, easy to access database of barcodes I could find is barcodelookup.com. When a barcode
is looked up, it goes to a url formatted as barcodelookup.com/barcode_value. The HTML of the
resulting page contains a meta content tag that says something along the lines of
"Barcode for [barcode_value] - [name of the product]", so here we get the HTML using JSoup, try to
find the tag, split the string at the "-", and trim it to get rid of the space in at the start
and the dot at the end.
 */
package com.example.fridgelog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private String productName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        //We get permission to use the camera
        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission())
            {
                requestPermission();
            }
        }
    }
    //Most of this shit I copied from a YouTube video lmao, anyway, I don't think this needs
    //an explanation
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                if(scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else
            {
                requestPermission();
            }
        }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted)
                    {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            if (shouldShowRequestPermissionRationale(CAMERA))
                            {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(BarcodeScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result)
    {
        //We get the barcode value as a string and begin the processing
        final String myResult = result.getText();
        codeToProductName(myResult);
    }
    private void codeToProductName(String code)
    {
        //We format the url
        String url = "https://www.barcodelookup.com/" + code;

        try
        {
            //and try to turn it into a product name
            UrlToProductParser urlToProductParser= new UrlToProductParser();
            urlToProductParser.execute(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class UrlToProductParser extends AsyncTask<String, Void, String>
    {
        //Network operations need to be AsyncTasks because they can't be run in the main thread
        //to avoid network related delays. In this case it's kinda pointless because we have to
        //wait for the result anyway, but it had to be done like this.
        @Override
        protected void onPreExecute()
        {
            //We create a circle spinning dialog thingy while we wait
            progressDialog = new ProgressDialog(BarcodeScanner.this);
            progressDialog.setMessage("Getting product name...");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings)
        {
            //We set the product name to a blank string by default, in case we don't find the
            //product name, so that we don't send back null
            String productNamePostProc = "";
            String url = strings[0];
            try
            {
                //We get the HTML
                final Document document = Jsoup.connect(url).get();
                //and assume by default that we did not find the product name
                boolean scrapeSuccessful = false;

                //First we look for meta[content] tags
                for (Element row:document.select("meta[content]"))
                {
                    //Then we get what's inside the actual content tag
                    String rowString = row.attr("content");
                    //There usually seem to be multiple meta[content] tags, so we pick the one that
                    //contains the word "Barcode"
                    if (rowString.contains("Barcode"))
                    {
                        scrapeSuccessful = true;
                        //Split it at the "-"
                        String[] split = rowString.split("-");
                        String productNamePreProc = split[1];
                        //then trim the space at the beginning and the dot at the end using
                        //StringBuilder
                        StringBuilder sb = new StringBuilder(productNamePreProc);
                        sb.deleteCharAt(productNamePreProc.length()-1);
                        sb.deleteCharAt(0);
                        //and turn it back into a string
                        productNamePostProc = sb.toString();
                    }
                }

                if (!scrapeSuccessful)
                {
                    //If we fail to find the product name, we just return the blank string
                    return productNamePostProc;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return productNamePostProc;

        }

        @Override
        protected void onPostExecute(String s)
        {
            //Dismiss the dialog
            progressDialog.dismiss();
            //Set the class-wide variable to the product name we found, because as far as I can tell,
            //there is no other way to return it
            productName = s;
            Intent resultIntent = new Intent();
            //And send it back to the AddItemFragment
            resultIntent.putExtra("result",productName);
            setResult(RESULT_OK,resultIntent);
            finish();
        }
    }
}