package com.example.pantry1;
/*
1. BARCODE SCANNER - how to on youtube https://www.youtube.com/watch?v=iMFFdQykL0o  note this is a Java tutorial

//put this dependency in: gradle scripts > build gradle(module:app) - slot in after the appcompat one, then click sync now (top right)
//why? to import the necessary classes
dependencies {
    implementation 'com.budiyev.android:code-scanner:2.1.0'
}
//create a layout file
//Why? to determine how the screen will look when its about to scan something
res > layout > file > new > layout resource file > name it main_activity or whatever you want

//retrieve the code called 'define a view in your layout file' from https://github.com/yuriy-budiyev/code-scanner and paste it
//into the new layout file, overwriting what is there and run, you can then amend the layout and colour,
//if get an error change the minSdkversion in gradle scripts > build.gradle > module:app

//Create a new Java class
Java > rc com.example.appname > name it mainActivity or similar

//Import
in mainActivity, import androidx.appcompat.app.AppCompatActivity;

//Extend
public class MainActivites extends AppCompatActivity {
}

//Paste the Java code section from https://github.com/yuriy-budiyev/code-scanner inside the {} and import the missing classes
//all of it except '{public class MainActivity extends AppCompatActivity'

//Clean the new code - check the layout name matches the layout file you created: setContentView(R.layout.main_activity);
//add your Java file name:  public void onDecoded(@NonNull final Result result) {
//                MainActivites.this.runOnUiThre
// change the start of toast to:  Toast.makeText(MainActivities.this

//Check camera permissions by adding an if statement in onCreate

CodeScannerView scannerView = findViewById(R.id.scanner_view);
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS, 2002);
        }
        mCodeScanner = new CodeScanner(this, scannerView);

//Create the permissions instance variable to hold what we are asking permission for
public class MainActivites extends AppCompatActivity {

    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private CodeScanner mCodeScanner;

//maker scannerView global by putting it with the instance variables
private CodeScanner mCodeScanner;
    CodeScannerView scannerView;

//delete the scannerView variable declaration from the onCreate method
setContentView(R.layout.main_activity);
        scannerView = findViewById(R.id.scanner_view);
        if (Build.VERSION.SDK_INT >= 23) {

//remove mCodeScanner.startPreview(); from the onResume method

// add if (mCodeScanner != null:)
//            mCodeScanner.releaseResources();    to onPause

//get a list of override functions with ctrl + o and choose:

//@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startScanner() {

//Then check if permission has been granted
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2002) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) //note that grantResults can
                startScanner();
            else
                Toast.makeText(MainActivites.this, "I need camera permissions", Toast.LENGTH_LONG).show();
    }

    private void startScanner() {

    // add directly above application in AndroidManifest.xml
    <uses-permission android:name="android.permission.CAMERA" />

 */


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;


public class BarcodeReader extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    //added
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    CodeScannerView scannerView;
    String barcodeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        scannerView = findViewById(R.id.scanner_view);

        //added
        if (Build.VERSION.SDK_INT >= 23) {

            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS, 2002);
            }
            else
                startScanner();
        }
        else
            startScanner();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(BarcodeReader.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //ctrl + o and choose this method from the list of override methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2002){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startScanner();
            }
            else
                Toast.makeText(BarcodeReader.this, "You need camera permissions", Toast.LENGTH_LONG).show();
        }
    }

    private void startScanner() { //end of added - cut and paste the following code to the method:
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.startPreview(); //start the scanner when the user clicks button add by barcode
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                BarcodeReader.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(BarcodeReader.this, result.getText(), Toast.LENGTH_LONG).show();
                        barcodeNumber = result.getText();
                        Intent intent = new Intent(BarcodeReader.this, Pantry.class);
                        intent.putExtra("BARCODE", barcodeNumber);
                        intent.putExtra("FROM", "bcr");
                        //intent.putExtra("FROM", "1" );

                        startActivity(intent);
                        finish();

                    } //result.getText() returns the barcode number from the barcode that has been scanned
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }  //this curly was added when the new method was created, the method body is code already here
    //end of added
    @Override
    protected void onResume() {
        super.onResume();
        // removed mCodeScanner.startPreview();
        //TextView barcode = findViewById(R.id.barcode);
        //barcode.setText(barcodeNumber);
        //Intent intent = new Intent(this, ShowBarcode.class);
    }

    @Override
    protected void onPause() {
        //added
        if(mCodeScanner != null)
            //end of added - remember to indent the line after this if statement
            mCodeScanner.releaseResources();
        super.onPause();
    }

    //@Override
    //public void onBackPressed() {
        //super.onBackPressed();
        //Intent intent1 = new Intent(this, Bread.class);
        //startActivity(intent1);
        //finish();
    //}
}


/*

BARCODE SCANNER - HOW TO

BARCODE SCANNER - how to on youtube https://www.youtube.com/watch?v=iMFFdQykL0o  note this is a Java tutorial

//put this dependency in: gradle scripts > build gradle(module:app) - slot in after the appcompat one, then click sync now (top right)
//why? to import the necessary classes
dependencies {
    implementation 'com.budiyev.android:code-scanner:2.1.0'
}
//create a layout file
//Why? to determine how the screen will look when its about to scan something
res > layout > file > new > layout resource file > name it main_activity or whatever you want

//retrieve the code called 'define a view in your layout file' from https://github.com/yuriy-budiyev/code-scanner and paste it
//into the new layout file, overwriting what is there and run, you can then amend the layout and colour,
//if get an error change the minSdkversion in gradle scripts > build.gradle > module:app

//Create a new Java class
Java > rc com.example.appname > name it mainActivity or similar

//Import
in mainActivity, import androidx.appcompat.app.AppCompatActivity;

//Extend
public class MainActivites extends AppCompatActivity {
}

//Paste the Java code section from https://github.com/yuriy-budiyev/code-scanner inside the {} and import the missing classes
//all of it except '{public class MainActivity extends AppCompatActivity'

//Clean the new code - check the layout name matches the layout file you created: setContentView(R.layout.main_activity);
//add your Java file name:  public void onDecoded(@NonNull final Result result) {
//                MainActivites.this.runOnUiThre
// change the start of toast to:  Toast.makeText(MainActivities.this

//Check camera permissions by adding an if statement in onCreate

CodeScannerView scannerView = findViewById(R.id.scanner_view);
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS, 2002);
        }
        mCodeScanner = new CodeScanner(this, scannerView);

//Create the permissions instance variable to hold what we are asking permission for
public class MainActivites extends AppCompatActivity {

    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private CodeScanner mCodeScanner;

//maker scannerView global by putting it with the instance variables
private CodeScanner mCodeScanner;
    CodeScannerView scannerView;

//delete the scannerView variable declaration from the onCreate method
setContentView(R.layout.main_activity);
        scannerView = findViewById(R.id.scanner_view);
        if (Build.VERSION.SDK_INT >= 23) {

//remove mCodeScanner.startPreview(); from the onResume method

// add if (mCodeScanner != null:)
//            mCodeScanner.releaseResources();    to onPause

//get a list of override functions with ctrl + o and choose:

//@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startScanner() {

//Then check if permission has been granted
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2002) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) //note that grantResults can
                startScanner();
            else
                Toast.makeText(MainActivites.this, "I need camera permissions", Toast.LENGTH_LONG).show();
    }

    private void startScanner() {

//The full code for bar scanner is:
-------------------------------------------
package com.example.barcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class MainActivites extends AppCompatActivity {

    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        scannerView = findViewById(R.id.scanner_view);
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS, 2002);
            }
            else
                startScanner();
        }
        else
            startScanner();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2002) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startScanner();
            else
                Toast.makeText(MainActivites.this, "I need camera permissions", Toast.LENGTH_LONG).show();
    }

    private void startScanner() {

        {

            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    MainActivites.this.runOnUiThread(new Runnable() {
                        @Override
                        public <MainActivities> void run() {
                            Toast.makeText(MainActivities.this, result.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });

    }

}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null:)
            mCodeScanner.releaseResources();
        super.onPause();
    }
}

    private void startScanner() {
    }
}
//------------------end of full code for bar scanner-----------------

**/