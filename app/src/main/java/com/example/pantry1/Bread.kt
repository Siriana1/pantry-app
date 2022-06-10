package com.example.pantry1

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantry1.Pantry.Companion.category
import com.sqlitedemo.DatabaseHandler
import com.sqlitedemo.DatabaseHandler.Companion.from2
import kotlinx.android.synthetic.main.activity_bread1.*
import kotlinx.android.synthetic.main.activity_bread1.btnAddNew
import kotlinx.android.synthetic.main.activity_bread1.btnAddWithScanner
import kotlinx.android.synthetic.main.activity_bread1.btnSubmitCatName
import kotlinx.android.synthetic.main.activity_bread1.etEditCatName
import kotlinx.android.synthetic.main.activity_bread1.ibEditCatName
import kotlinx.android.synthetic.main.activity_bread1.llBarcodeAdd
import kotlinx.android.synthetic.main.activity_bread1.llItemAdd
import kotlinx.android.synthetic.main.activity_bread1.llQtyAdd
import kotlinx.android.synthetic.main.add_item.btnAdd
import kotlinx.android.synthetic.main.add_item.etBarcode
import kotlinx.android.synthetic.main.add_item.etItem
import kotlinx.android.synthetic.main.add_item.etQuantity
import kotlinx.android.synthetic.main.add_item_dialog.*
import kotlinx.android.synthetic.main.duplicate_item_alert_dialog.*
import kotlinx.android.synthetic.main.update_dialog.*

class Bread : AppCompatActivity() {

    companion object{
        var newCatName = "default"
        var qtyUpdateType1 = ""
    }

    var barcode = ""
    var from = ""
    val catCode = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bread)

        //RETRIEVE THE BARCODE that was sent over by startScanner() from BarcodeReader.java or ThrowOutByScanner.java
        val intent = intent
        barcode = intent.getStringExtra("BARCODE").toString()
        from = intent.getStringExtra("FROM").toString()

        if(category == "chosen"){
            enterDetailsExceptBarcode()
        }

        //onclick event for the home button
        homeFromBread.setOnClickListener {
            Toast.makeText(this, "This is the homepage", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //--------------EDIT CATEGORY NAME----------------------------------------------------------------

        //on click event for the tvCatNameBread (category name inside the category) button
        tvCatNameBread.setOnClickListener { view ->
            if(ibEditCatName.visibility == View.GONE){
                ibEditCatName.visibility = View.VISIBLE
            }
            else if(ibEditCatName.visibility == View.VISIBLE){
                ibEditCatName.visibility = View.GONE
                etEditCatName.visibility = View.GONE
                btnSubmitCatName.visibility = View.GONE
            }
        }
        //on click event for the ibEditCatName (edit pencil next to the cat name inside the cat)
        ibEditCatName.setOnClickListener { view ->
            etEditCatName.visibility = View.VISIBLE
            btnSubmitCatName.visibility = View.VISIBLE
        }
        //on click event for btnSubmitCatName (to change the name of a category)
        btnSubmitCatName.setOnClickListener { view ->
            newCatName = etEditCatName.text.toString()
            tvCatNameBread.text = "$newCatName"
            val intent = Intent(this, Pantry::class.java)
            intent.putExtra("NEW_CAT_NAME", newCatName)
            intent.putExtra("FROM", "new_category_name")
            startActivity(intent)
            finish()
        }
        //------------END OF EDIT CATEGORY NAME------------------------------------------------------------------

        //on click event for the submit (Add Item) button - to add a new item by barcode
        btnAddWithScanner.setOnClickListener { view ->
            addRecordByBarcode()
        }
        //------------------------------ADD NEW ITEM MANUALLY-----------------------------------------------------------

        //on click event for the manually add new item button '+'
        //makes the edit texts visible so the user can enter the details of the new item -
        // maybe should use a dialog instead?
        btnAddNew.setOnClickListener { view ->

            addNewItemDialog()

            //btnAdd.visibility = View.VISIBLE
            //llQtyAdd.visibility = View.VISIBLE
            //llBarcodeAdd.visibility = View.VISIBLE
            //llItemAdd.visibility = View.VISIBLE
        }
        //on click event for the submit (Add Item) button - to add a new item manually
        btnAdd.setOnClickListener { view ->
            addRecord(view)
        }
        //--------------------------END OF ADD NEW ITEM MANUALLY--------------------------------------------------------

        //Displays the list of items as it currently is when the category is opened
        // - gets the items from the db and displays them in the recycler view
        setupListofDataIntoRecyclerView()

        setCategoryName()

    } //-----------------END OF ONCREATE-------------------------------------------------------------------------------

    //if the user has changed the category heading, this function updates it to the one the user chose
    private fun setCategoryName() {
        if(newCatName != "default"){
            tvCatNameBread.text = newCatName
        }
    }
    //goes back one page when the user clicks the phone's back button
    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, Pantry::class.java)
        startActivity(intent)
        finish()
    }

    // makes the name and quantity EditTexts visible, along with the 2nd add item button,
    // the barcode has been added separately with the scanner
    fun enterDetailsExceptBarcode(){

        // as the item's barcode number does not exist in the db, prompt the user to enter the item's details

        val addItemDialog = Dialog(this, R.style.Theme_Dialog) // create a dialog object and set the style
        addItemDialog.setCancelable(false) // prevent the dialog box from closing if user clicks outside of it
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        addItemDialog.setContentView(R.layout.add_item_bc_dialog) // add the page layout to the dialog object

        // set values to default
        addItemDialog.etAddName.setText("")
        addItemDialog.etAddQty.setText(1.toString()) // "" +  also would turn it into a string

        // when the user clicks btnAdd (the ADD ITEM button) on the dialog box
        addItemDialog.btnAdd.setOnClickListener {

            // capture the user-decided values
            val name = addItemDialog.etAddName.text.toString()
            val quantity = addItemDialog.etAddQty.text.toString().toInt()
            from2 = "1" //tell db which table to update
            val databaseHandler: DatabaseHandler =
                DatabaseHandler(this) //make an instance of DatabaseHandler

            if (!name.isEmpty() && quantity >= 0) {
                val status =
                    databaseHandler.addItem((Item(0, catCode, name, barcode, quantity)))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Item added.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()

                    addItemDialog.dismiss() // Dialog and keyboard will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Quantity cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //onclick listener for the dialog's cancel button
        addItemDialog.btnCancel.setOnClickListener {
            addItemDialog.dismiss()
        }

        //Start the dialog and display it on screen.
        addItemDialog.show()

        /* btnAddWithScanner.visibility = View.VISIBLE
        btnAdd.visibility = View.GONE
        llQtyAdd.visibility = View.VISIBLE
        llItemAdd.visibility = View.VISIBLE */

        category = ""

    }

    //this function gets the items from the db and displays them on the page
    fun setupListofDataIntoRecyclerView() {

        if (getItemsBread().size > 0) { //check there are some items in the db

            rvItemsListBread.visibility = View.VISIBLE
            tvNoRecordsAvailableBread.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsListBread.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsListBread.adapter = itemAdapter
        } else { //if there are no items in the db, display the 'no items available' message

            rvItemsListBread.visibility = View.GONE
            tvNoRecordsAvailableBread.visibility = View.VISIBLE
        }
    }

    private fun getItemsList(): ArrayList<DataModel> {

        val dmList = ArrayList<DataModel>()

        val items = getItemsBread()
        var count = 0
        while (count < items.size){
            dmList.add(DataModel(items[count], 2))
            count++
        }

        return dmList
    }

    /**
     * Function is used to get the Items from the database
     */
    fun getItemsBread(): ArrayList<Item> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        from2 = "1" //this is set when the category opens, and is how the db knows which table to update
        // if the user chooses to add, update or delete items

        //calling the viewItem method of DatabaseHandler class to read the records
        val itemList: ArrayList<Item> = databaseHandler.viewItem()

        return itemList
    }

    //Retrieve Items from the database from a table specified by from2 (set by the calling function)
    fun getItems(): ArrayList<Item> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //calling the viewItem method of DatabaseHandler class to read the records
        val itemList: ArrayList<Item> = databaseHandler.viewItem()

        return itemList
    }

    //method for saving records in database
    fun addRecord(view: View) {
        from2 = "1" //tell the db to add the item to the bread table
        //retrieve user inputs
        val name = etItem.text.toString()
        val barcode = etBarcode.text.toString()
        val quantity = etQuantity.text.toString().toInt()
        //make a new instance of the DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!name.isEmpty()) { //check user has entered the item name
            val status = //store whether the item was successfully added
                databaseHandler.addItem(Item(0, catCode, name, barcode, quantity)) //add the item to the database
            if (status > -1) { //if item was added
                etItem.text.clear()
                etBarcode.setText("0") //needs set to 0
                etQuantity.setText("1") //needs set to 1

                btnAdd.visibility = View.GONE
                llQtyAdd.visibility = View.GONE
                llBarcodeAdd.visibility = View.GONE
                llItemAdd.visibility = View.GONE

                setupListofDataIntoRecyclerView()

                Toast.makeText(
                    applicationContext,
                    "Item successfully added!",
                    Toast.LENGTH_LONG).show()
                val intent = Intent(this, BarcodeReader::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name cannot be blank and quantity must be a positive number",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //method for saving records in database by barcode
    fun addRecordByBarcode() {
        from2 = "1"  //tell db which table to add the item to
        if (barcode != ""){
            val name = etItem.text.toString() //retrieve the user input for item name
            val quantity = etQuantity.text.toString().toInt()  //retrieve the user input for item quantity
            val databaseHandler = DatabaseHandler(this)
            if (!name.isEmpty()) { //if the user has entered the item name
                val status =
                    databaseHandler.addItem(
                        Item(
                            0,
                            catCode,
                            name,
                            barcode,
                            quantity
                        )
                    ) //add the item to the db
                if (status > -1) { //if status contains a number that indicates the item was successfully added
                    Toast.makeText(
                        applicationContext,
                        "Item successfully added!",
                        Toast.LENGTH_LONG
                    ).show()
                    // Reset the EditText fields for entering an item's details
                    etItem.text.clear()
                    etBarcode.setText("0")
                    etQuantity.setText("1")
                    // Disappear the views associated with entering an item's details, leaving just the list of items
                    btnAddWithScanner.visibility = View.GONE
                    llQtyAdd.visibility = View.GONE
                    llItemAdd.visibility = View.GONE
                    //Retrieve the data from the db and display it on the page
                    setupListofDataIntoRecyclerView()  // this page as context is assumed here
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "An item was not added",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Method is used to show the Custom Dialog.
     */
    fun updateRecordDialog(item: Item) { // takes an existing item
        val updateDialog = Dialog(this, R.style.Theme_Dialog) // create a dialog object and set the style
        updateDialog.setCancelable(false) // prevent the dialog box from closing if user clicks outside of it
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setContentView(R.layout.update_dialog) // add the page layout to the dialog object

        // set the values to that of the existing item
        updateDialog.etUpdateName.setText(item.name)
        updateDialog.etUpdateBarcode.setText(item.barcode)
        updateDialog.etUpdateQuantity.setText(item.quantity.toString()) // "" +  also would turn it into a string

        // when the user clicks the word 'Update' on the dialog box
        updateDialog.tvUpdate.setOnClickListener {

            // capture the user updated values
            val name = updateDialog.etUpdateName.text.toString()
            val barcode = updateDialog.etUpdateBarcode.text.toString()
            val quantity = updateDialog.etUpdateQuantity.text.toString().toInt()
            from2 = "1" //tell db which table to update
            val databaseHandler: DatabaseHandler =
                DatabaseHandler(this) //make an instance of DatabaseHandler

            if (!name.isEmpty() && quantity >= 0) {
                val status =
                    databaseHandler.updateItem(Item(item.id, item.catCode, name, barcode, quantity))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Item Updated.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()

                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Quantity cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        updateDialog.tvCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        updateDialog.tvDelete.setOnClickListener(){
            deleteRecordAlertDialog(item)
            updateDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        updateDialog.show()
    }


    // Method is used to update the quantity of an item when the user clicks add one, eat one or throw one out
    fun updateQuantity(item: Item) {

        val name = item.name
        val barcode = item.barcode
        val quantity = item.quantity

        from2 = "1" //tell the db which table to update
        val databaseHandler: DatabaseHandler =
            DatabaseHandler(this) //make an instance of DatabaseHandler

        if (!name.isEmpty() && quantity >= 0) {
            val status =
                databaseHandler.updateItem(Item(item.id, catCode, name, barcode, quantity))
            if (status > -1) {
                if (qtyUpdateType1 == "throw out") {
                    Toast.makeText(applicationContext, "Item thrown out", Toast.LENGTH_SHORT).show()
                }else if (qtyUpdateType1 == "eaten") {
                    Toast.makeText(applicationContext, "Item " +
                            "successfully moved to shopping list!", Toast.LENGTH_SHORT).show()
                }else if (qtyUpdateType1 == "increment") {
                    Toast.makeText(applicationContext, "Item successfully added!", Toast.LENGTH_SHORT).show()
                }else if (Bread.qtyUpdateType1 == "moveToSL") { //if user has clicked the minus sign
                    from2 = "0" // tell the db it is the shopping list table we are interested in
                    val slItems = getItems()  // retrieve all items from the shopping list table
                    var itemCount = 0
                    for (i in slItems) {
                        if(i.name == name) {  // check if the item is already on the shopping list table
                            itemCount++
                            i.quantity++
                            databaseHandler.updateItem(Item(i.id, catCode, i.name, i.barcode, i.quantity))
                        }
                    }
                    if(itemCount == 0) { // if item is not on the shopping list, create it
                        databaseHandler.addItem(Item(item.id, catCode, item.name, item.barcode, 1))
                    }
                    Toast.makeText(applicationContext, "Item successfully moved to shopping list!",
                        Toast.LENGTH_SHORT).show()
                }

                setupListofDataIntoRecyclerView()
            }
         // Delete an item
        }else if(quantity < 0){ // if quantity is 0 and user clicks the minus or bin sign again
            item.quantity = 0
            deleteRecordAlertDialog(item)
        }
    }

    // Method is used to show the Add New Item Dialog.  Runs when user clicks btnAddNew (+)

    fun addNewItemDialog() {
        val addItemDialog = Dialog(this, R.style.Theme_Dialog) // create a dialog object and set the style
        addItemDialog.setCancelable(false) // prevent the dialog box from closing if user clicks outside of it
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        addItemDialog.setContentView(R.layout.add_item_dialog) // add the page layout to the dialog object

        // set values to default
        addItemDialog.etAddName.setText("")
        addItemDialog.etAddBarcode.setText("0")
        addItemDialog.etAddQty.setText(1.toString()) // "" +  also would turn it into a string

        // when the user clicks btnAdd (the ADD button) on the dialog box
        addItemDialog.btnAdd.setOnClickListener {

            // capture the user-decided values
            val name = addItemDialog.etAddName.text.toString()
            val barcode = addItemDialog.etAddBarcode.text.toString()
            val quantity = addItemDialog.etAddQty.text.toString().toInt()
            // check the item doesn't already exist
            val breadItems = getItemsBread()
            var duplicateItemFound = false
            for(i in breadItems){
                if(i.name == name && duplicateItemFound == false){
                    duplicateItemFound = true
                    duplicateItemDialog()  //display the duplicate item dialog
                    addItemDialog.dismiss()
                }
            }
            // if no duplicate item exists, continue with adding the new item
            if (duplicateItemFound == false){
                from2 = "1" //tell db which table to update
                val databaseHandler: DatabaseHandler =
                    DatabaseHandler(this) //make an instance of DatabaseHandler

                if (!name.isEmpty() && quantity >= 0) {
                    val status =
                        databaseHandler.addItem((Item(0, catCode, name, barcode, quantity)))
                    if (status > -1) {
                        Toast.makeText(applicationContext, "Item added.", Toast.LENGTH_LONG).show()

                        setupListofDataIntoRecyclerView()

                        addItemDialog.dismiss() // Dialog will be dismissed
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Name or Quantity cannot be blank",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        //onclick listener for the dialog's cancel button
        addItemDialog.btnCancel.setOnClickListener {
            addItemDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        addItemDialog.show()
    }

    // Method is used to show the Duplicate Item Alert Dialog.  Runs when user clicks btnAddNew (+) but the item
    // already exists

    fun duplicateItemDialog() {
        val duplicateItemDialog = Dialog(this, R.style.Theme_Dialog) // create a dialog object and set the style
        duplicateItemDialog.setCancelable(false) // prevent the dialog box from closing if user clicks outside of it
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        duplicateItemDialog.setContentView(R.layout.duplicate_item_alert_dialog) // add the page layout to the dialog object

        // when the user clicks btnAddDiffItem (the ADD A DIFFERENT ITEM button) on the dialog box
        duplicateItemDialog.btnAddDiffItem.setOnClickListener {

            // dismiss the dialog and return to the add new item dialog
            duplicateItemDialog.dismiss()
            addNewItemDialog()
        }
        //onclick listener for the dialog's cancel button
        duplicateItemDialog.btnCancelAddItem.setOnClickListener {
            duplicateItemDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        duplicateItemDialog.show()
    }

    /**
     * Method is used to show the Alert Dialog.
     */
    fun deleteRecordAlertDialog(item: Item) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${item.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            from2 = "1" //tells db which table item is to be deleted from
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            //calling the deleteItem method of the DatabaseHandler class to delete a record
            //val status = databaseHandler.deleteItem(Item(item.id, "", "0", 1))
            val status = databaseHandler.deleteItem(Item(item.id, catCode, item.name, item.barcode, item.quantity))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Item deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}