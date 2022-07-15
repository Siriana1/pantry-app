package com.example.pantry1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sqlitedemo.DatabaseHandler
import com.sqlitedemo.DatabaseHandler.Companion.from2
import kotlinx.android.synthetic.main.activity_pantry.*

class Pantry : AppCompatActivity() {

    companion object{ //an object within a class that is auto-instantiated like a static class
        private const val ADD_CATEGORY_ACTIVITY_REQUEST_CODE = 1 //const val is like val but has to be there at compile time

        //variables in here do not update when the page opens
        const val CATNAME = "Category name"
        var cat1 = "Category name"

        var newCatName = "default"
        var from1 = "default"

        var category = ""
    }

    //instance variables for class Pantry - these update to the values given here whenever the page opens
    var from = ""
    var barcode = ""

    //methods of class Pantry

    //----------------START OF ONCREATE----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry)

        //Retrieve the updated category name, the preceding page and/or the barcode
        val intent = intent
        from = intent.getStringExtra("FROM").toString()  //to check if comes from bcr, remove or ThrowOutByScanner ("3")
        barcode = intent.getStringExtra("BARCODE").toString()
        from1 = intent.getStringExtra("FROM").toString()

        //check if this page was opened by the barcode reader
        if (from == "bcr" || from == "2" || from == "3"){ // bcr - add, 2 - remove, 3 - delete
            checkBarcode()
        }

        //updates a category's name on the main pantry page
        updateCategoryName()

        //function to check if new categories have been created and if so, display them on the main pantry page
        checkForNewCategories()

        //call this method when the + button (add new category button) is clicked
        //val addCategory_btn = findViewById<Button>(R.id.addCategory_btn)
        addCategory_btn.setOnClickListener {
            val intent = Intent(this, addCategory::class.java) // in the ui, move from this page
            // (activity_pantry.xml) to the activity_add_category.xml page (the xml file linked to addCategory.kt)
            startActivityForResult(intent, ADD_CATEGORY_ACTIVITY_REQUEST_CODE) //part 1/3 of the collaboration
        }

        // On click event for the home button
        home.setOnClickListener(){
            Toast.makeText(this, "This is the homepage", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    //----------------END OF ONCREATE---------------------------------------------------------------------------------

    //Checks if the scanned barcode exists in the db and updates it accordingly if so.  If not found sends it to the
    //appropriate category to be added.
    fun checkBarcode(){
        var cat = 1
        var existingBarcode = false
        while(cat < 3 && existingBarcode == false) { //cycle through the categories looking for the barcode
            var items = getItems(cat)
            for (item in items)
                if (item.barcode == barcode) { // if item has already been added
                    existingBarcode = true // scanned item has been found in the pantry
                    // Toast.makeText(applicationContext,"Item found in pantry",Toast.LENGTH_LONG).show()
                    if (from == "3") { //if the user has selected to THROW OUT THE ITEM by barcode
                        var iQty = item.quantity
                                if (item.quantity > 0){
                                    item.quantity--
                                    Toast.makeText(
                                        applicationContext,
                                        "Item successfully thrown out.  There are now ${item.quantity} left",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this, ThrowOutByScanner::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(applicationContext, "There are no ${item.name}s",
                                        Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, ThrowOutByScanner::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                    } else if(from == "2"){  // if user has clicked REMOVE under the barcode section
                        if (item.quantity > 0){  // if there are any items of this type in the cateogory
                            item.quantity--
                            Toast.makeText(applicationContext,"Item successfully moved to shopping list and item" +
                                    " quantity is now ${item.quantity}", Toast.LENGTH_LONG).show()
                        }else{ // if quantity is 0
                            Toast.makeText(applicationContext, "There are no ${item.name}s",
                                Toast.LENGTH_LONG).show()
                        }

                        val dbhSl = DatabaseHandler(this)
                        //  Increment the item' quantity on the shopping list
                        from2 = "0" // tell the db which category to update
                        val slItems = getItems(0)  // retrieve all items from the SL
                        var itemFound = false
                        for (sli in slItems) {
                            if(sli.name == item.name) {  // check if the item is already on the shopping list table
                                itemFound = true
                                sli.quantity++
                                dbhSl.updateItem(Item(sli.id, sli.catCode, sli.name, sli.barcode, sli.quantity))
                            }
                        }
                        if(itemFound == false) { // if item is not on the SL, create it
                            dbhSl.addItem(Item(item.id, item.catCode, item.name, item.barcode, 1))
                        }

                    } else {  // if user has clicked ADD under the barcode reader section
                        item.quantity++
                        Toast.makeText(applicationContext, "Item added", Toast.LENGTH_SHORT).show()
                        //val intent = Intent(this, BarcodeReader::class.java)
                        //startActivity(intent)
                        //finish()
                    }

                    var dbh = DatabaseHandler(this)
                    from2 = item.catCode
                    dbh.updateItem(item)
                }
            cat++
        }
        // For when the barcode of the item that the user is attempting to decrement by scanner is not in the db
        if(from == "3" && !existingBarcode){ //if page opened by ThrowOutByScanner and the barcode is not in the db
            Toast.makeText(applicationContext, "Item not found", Toast.LENGTH_LONG).show()
            //val intent = Intent(this, ThrowOutByScanner::class.java)
            //startActivity(intent)
            //finish()
        }

        if (existingBarcode == false && from == "bcr"){ //if item doesn't exist in the database with a barcode
            category = "chosen" // tell the chosen category to open the add_item_bc_dialog
            tvPickCat.visibility = View.VISIBLE  //make text view appear asking user to pick a category
        }

        val intent = Intent(this, MainActivity::class.java)
        if (from ==  "3"){ // if the user is deleting things
            intent.putExtra("FROM", "send_to_scanner_3")
            startActivity(intent)
            finish()
        }else if (from == "2"){  // if the user is moving items to the shopping list
            intent.putExtra("FROM", "send_to_scanner_2")
            startActivity(intent)
            finish()
        } else if(from == "bcr"){
            intent.putExtra("FROM", "send_to_scanner_1")  // if user is adding items
            startActivity(intent)
            finish()
        }
    }

    //Retrieves Items from the database
    fun getItems(catNo: Int): ArrayList<Item> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //variable from2 is in the companion object in the DatabaseHandler.kt class
            DatabaseHandler.from2 = catNo.toString()
            //calling the viewItem method of DatabaseHandler class to read the records
            val itemList: ArrayList<Item> = databaseHandler.viewItem()

            return itemList
    }

    //function updates a category's name if a user has changed the name
    private fun updateCategoryName() {
        if(from1 == "new_category_name"){
            newCatName = intent.getStringExtra("NEW_CAT_NAME").toString()
            btnBread.text = newCatName
        }else if(newCatName != "default"){
            btnBread.text = newCatName
        }
    }

    //determines what happens when the phone's back button is pressed - the super (default) exits the app
    override fun onBackPressed() {
        //super.onBackPressed();
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //function to check if new categories have been created and if so, display them
    fun checkForNewCategories(){
        if(cat1 != "Category name"){
            newCategory_btn.text = cat1
            newCategory_btn.visibility = View.VISIBLE
        }else{newCategory_btn.visibility = View.INVISIBLE}
    }

    //part 3/3 of the collaboration
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
            if(requestCode == ADD_CATEGORY_ACTIVITY_REQUEST_CODE){
                if(data != null){ //implement the new category button as follows:
                    val catName = data.getStringExtra(CATNAME)
                    cat1 = catName.toString()
                    Toast.makeText(this, "CAT1 CREATED!", Toast.LENGTH_LONG).show()
                    newCategory_btn.text = "$catName"
                    newCategory_btn.visibility = View.VISIBLE
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                Log.e("Cancelled", "Cancelled")
                Toast.makeText(this, "A new category was not added", Toast.LENGTH_SHORT).show()
            }//add on back pressed to have a message display for that as well as the home button
    }
    //------------- END OF ADD A CATEGORY -----------------------------------------------------------------


    //--------------START OF ON CLICK EVENTS FOR THE PANTRY CATEGORIES --------------------------------------

    //on click event for the 'Baking' button in Pantry
    fun openBaking(view: View) {
        // Launching the Baking/History Activity
        Toast.makeText(this, "Welcome to the baking category", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Baking::class.java)
        //check if the user is choosing a category to add a new item to by barcode
        if (category == "chosen"){
            intent.putExtra("BARCODE", barcode)
            intent.putExtra("FROM", "pantry_with_bc")
        } else {   //otherwise just open Bread as normal
            intent.putExtra("FROM", "from_pantry")
        }
        startActivity(intent)
        finish()
    }

    //on click event for the 'Bread/pastries' button in Pantry
    fun openBread(view: View) {
        // Launching the Bread Activity
        Toast.makeText(this, "Welcome to the breads and pastries category", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Bread::class.java)
        if (category == "chosen"){ //check if the user is choosing a category to add a new item to by barcode
            intent.putExtra("BARCODE", barcode)
        } else {
            intent.putExtra("FROM", "from_pantry") //otherwise just open Bread as normal
        }
        startActivity(intent)
        finish()
    }

    //--------------END OF ON CLICK EVENTS FOR THE PANTRY CATEGORIES --------------------------------------

}