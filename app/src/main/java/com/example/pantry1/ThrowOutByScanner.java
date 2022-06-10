package com.example.pantry1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ThrowOutByScanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    //added
    String[] PERMISSIONS = {Manifest.permission.CAMERA};
    CodeScannerView scannerView;
    String barcodeNumber;
    String from = "";
    String scannerType = "0";

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
        Intent intent = new Intent(ThrowOutByScanner.this,MainActivity.class);
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
                Toast.makeText(ThrowOutByScanner.this, "You need camera permissions", Toast.LENGTH_LONG).show();
        }
    }

    private void startScanner() { //end of added - cut and paste the following code to the method:
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.startPreview(); //start the scanner
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                ThrowOutByScanner.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(ThrowOutByScanner.this, result.getText(), Toast.LENGTH_SHORT).show();
                        barcodeNumber = result.getText();// retrieves the barcode that has been scanned
                        Intent intent = new Intent(ThrowOutByScanner.this, Pantry.class);
                        intent.putExtra("BARCODE", barcodeNumber);
                        scannerType = getIntent().getStringExtra("SCANNER_TYPE"); // is it
                        // from add, remove, or delete
                        Toast.makeText(
                                ThrowOutByScanner.this,
                                "Scan type is " + scannerType,
                                Toast.LENGTH_LONG
                        ).show();
                        if(Integer.parseInt(scannerType) == 2){
                            intent.putExtra("FROM", "2");

                        }else if (Integer.parseInt(scannerType) == 3){
                            intent.putExtra("FROM", "3" );
                        }

                        startActivity(intent);
                        finish();

                    }
                });
            }
        });
        // Starts the scanner
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
