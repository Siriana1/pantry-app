package com.example.pantry1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bread1.*
import kotlinx.android.synthetic.main.activity_bread1.btnAdd
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_item.*

class MainActivity : AppCompatActivity() { //starting point for the app, this page opens when app is launched

    var barcode = ""
    var from = ""
    var from3 = "default" // for checking if its from the scanner

    //class MainActivity's methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // from3 is to check if came from the scanner and therefore should be re-opened so user can scan another item
        from3 = intent.getStringExtra("FROM").toString()  // check if should go back to the scanner
        if (from3 == "send_to_scanner_3"){  // if deleting
            val intent = Intent(this, ThrowOutByScanner::class.java)
            intent.putExtra("SCANNER_TYPE", "3")
            startActivity(intent)
            finish()
        }else if(from3 == "send_to_scanner_2"){ // if moving to SL
            val intent = Intent(this, ThrowOutByScanner::class.java)
            intent.putExtra("SCANNER_TYPE", "2")
            startActivity(intent)
            finish()
        }else if(from3 == "send_to_scanner_1") { // if adding
            val intent = Intent(this, BarcodeReader::class.java)
            startActivity(intent)
            finish()
        }

        // on click event for the home button
        btn_home.setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // on click event for the shopping list button
        btnShoppingList.setOnClickListener { view ->
            val intent = Intent(this, ShoppingList::class.java)
            startActivity(intent)
            finish()
        }

        //on click event for the add by barcode button
        btnAddByScanner.setOnClickListener { view ->
            val intent = Intent(this, BarcodeReader::class.java)
            startActivity(intent)
            finish()
            //btnAdd.visibility = View.VISIBLE
        }

        //on click event for the throw out by barcode button
        btnThrowOutByScanner.setOnClickListener { view ->
            val intent = Intent(this, ThrowOutByScanner::class.java)
            intent.putExtra("SCANNER_TYPE", "3")
            startActivity(intent)
            finish()
        }
        //on click event for the throw out by barcode button
        btnRemoveByScanner.setOnClickListener {
            val intent = Intent(this, ThrowOutByScanner::class.java)
            intent.putExtra("SCANNER_TYPE", "2")
            startActivity(intent)
            finish()
        }
        //on click event for the REMOVE button - to use an item by barcode
        //btnRemoveByScanner.setOnClickListener{
        // removeItemByBarcode()
        //}
    }

    // ---------------- end of on create ------------------------------------------------------------------------

   //on click event for the pantry button
    fun pantry(view: View){
        Toast.makeText(this, "This is the pantry", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Pantry::class.java)
        startActivity(intent)
        finish()
    }
    //on click event for the add item by barcode button
    fun addItemByBarcode(view: View){
        Toast.makeText(this, "This is the homepage", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    //on click event for the help button
    fun help(view: View){
        Toast.makeText(this, "Welcome to the help page", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Help::class.java)
        startActivity(intent)
        finish()
    }
}