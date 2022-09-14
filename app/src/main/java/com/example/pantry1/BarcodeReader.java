package com.example.pantry1;

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

    }  
    
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
