package com.example.pantry1

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantry1.ItemAdapter.Companion.VIEW_TYPE1
import com.example.pantry1.ItemAdapter.Companion.VIEW_TYPE2
import com.sqlitedemo.DatabaseHandler
import com.sqlitedemo.DatabaseHandler.Companion.from2
import kotlinx.android.synthetic.main.activity_baking.*
import kotlinx.android.synthetic.main.activity_baking.llBarcodeAdd
import kotlinx.android.synthetic.main.activity_baking.llItemAdd
import kotlinx.android.synthetic.main.activity_baking.llQtyAdd
import kotlinx.android.synthetic.main.activity_bread1.*
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.android.synthetic.main.add_item.btnAdd
import kotlinx.android.synthetic.main.add_item.etBarcode
import kotlinx.android.synthetic.main.add_item.etItem
import kotlinx.android.synthetic.main.add_item.etQuantity
import kotlinx.android.synthetic.main.add_item.tvNoRecordsAvailable
import kotlinx.android.synthetic.main.add_item_dialog.*
import kotlinx.android.synthetic.main.duplicate_item_alert_dialog.*
import kotlinx.android.synthetic.main.items_row.*
import kotlinx.android.synthetic.main.update_dialog.*

class ShoppingList : AppCompatActivity() {

    companion object {
        var qtyUpdateType0 = ""
    }

    var barcode = "" //to hold the barcode number if customer scans an item
    var from = "" //to record which page the intent came from
    val catCode = "0" //the category code for shopping list
    var catCodeSL = "0"  // default for the category code of shopping list items

//------------- START OF ONCREATE ---------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)

        from2 = "0"
        from = intent.getStringExtra("FROM").toString()

        // For if the user is adding a new item to the shopping list
        if (from == "cat picker") { // check if they have picked a category for their new SL item
            catCodeSL = intent.getStringExtra("CATCODESL").toString()  // retrieve the category code for the
            // category they have chosen for their new SL item

            addNewItemDialog() // opens the dialog that asks for details of the new SL item
        }

        //onclick event for the home button
        homeFromShopping.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //on click event for the add new shopping item button '+'
        btnAddNewShoppingItem.setOnClickListener { view ->
            val intent = Intent(this, CatPickerDialog::class.java)
            startActivity(intent)
            finish()
        }

        //on click event for the submit (Add Item) button on add_item.xml (the page that opens when user clicks
        // btnAddNewShoppingItem on activity_shopping_list.xml) -to add a new item manually
        // TODO add_item.xml should be a dialog
        btnAdd.setOnClickListener { view ->
            addRecord(view)
        }
        // on click event for the buy all button
        btnBuyAll.setOnClickListener {
            buyAll()
        }

        //Displays the list of items as it currently is when the category is opened
        // - gets the items from the db and displays them in the recycler view
        setupListofDataIntoRecyclerView()
    }

    //------------- END OF ONCREATE------------------------------------------------------------------------------

    // Method moves all shopping list items to the pantry
    fun buyAll(){
        val dbh = DatabaseHandler(this) // Create an instance of DatabaseHandler
        val slItems =  getItemsAny("0")  // Retrieve all SL items
        for (sli in slItems){  // For each SL item
            // Update the pantry items and set the SL quantities to 0
            var sliFoundInPantry = false  // helper
            val pantryItems = getItemsAny(sli.catCode) // Get all items from the item's category
            for(i in pantryItems){ // For each item in the item's category
                if (i.name == sli.name && sliFoundInPantry == false){ // If the pantry item matches the item on the SL
                    i.quantity = i.quantity + sli.quantity  // Increment the pantry item's quantity by the SL item's qty
                    sliFoundInPantry = true
                    from2 = i.catCode
                    Toast.makeText(applicationContext, "Shopping list item found in pantry: $sliFoundInPantry",
                        Toast.LENGTH_SHORT).show() // helper
                    dbh.updateItem(i)
                }
            }
            if(sliFoundInPantry == false){ // if is on the SL but does not exist in pantry
                from2 = sli.catCode // tell dbh which pantry category to put sli into
                dbh.addItem(sli)
            }
            sli.quantity = 0
            from2 = "0"
            dbh.updateItem(sli)
        }
        setupListofDataIntoRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    //goes back one page when the user clicks the phone's back button
    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, Pantry::class.java)
        startActivity(intent)
        finish()
    }

    //this function gets the items from the db and displays them on the page
    fun setupListofDataIntoRecyclerView() {

        if (getItemsShopping().size > 0) { //check there are some items in the Shopping table

            rvShoppingList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvShoppingList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items - assign an adapter to the rv
            rvShoppingList.adapter = itemAdapter
            //tell ItemAdapter instance which design to use for the viewholder
            from2 = "0"
        } else { //if there are no items in the db, display the 'no items available' message

            rvShoppingList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    // Creates an instance of DataModel for each item on the shopping list
    fun getItemsList() : ArrayList<DataModel> {
        val dmList = ArrayList<DataModel>()

        if(from2 == "0"){
            val slItems = getItemsShopping()
            var count = 0
            while(count < slItems.size) {
                dmList.add(DataModel(slItems[count], VIEW_TYPE1))
                count++
            }
        }else { Toast.makeText(this, "Shopping list items not found", Toast.LENGTH_SHORT).show()
            /*val items = getItems()
            var count = 0
            while (count < items.size) {
                dmList.add(DataModel(items[count], VIEW_TYPE2))
                count++
            } */
        }

        return dmList
    }

    //Retrieve Items from the database
    fun getItemsShopping(): ArrayList<Item> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //variable is in the companion object in the DatabaseHandler.kt class
        from2 = "0"  //tell db to fetch the items from the Shopping table
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
        from2 = "0"  // 0 is the code for shopping
        val catCode = "0"
        val name = etItem.text.toString()
        val barcode = etBarcode.text.toString()
        val quantity = etQuantity.text.toString().toInt()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!name.isEmpty()) {
            val status =
                databaseHandler.addItem(Item(0, catCode, name, barcode, quantity))
            if (status > -1) {
                etItem.text.clear()
                etBarcode.setText("0") //needs set to 0
                etQuantity.setText("1") //needs set to 1

                btnAdd.hideKeyboard()
                btnAdd.visibility = View.GONE
                llQtyAdd.visibility = View.GONE
                llBarcodeAdd.visibility = View.GONE
                llItemAdd.visibility = View.GONE

                setupListofDataIntoRecyclerView()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name cannot be blank and quantity must be a positive number",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Method is used to show the Custom Dialog.
     */
    fun updateRecordDialog(item: Item) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog) //set the style of the dialog
        updateDialog.setCancelable(false) //stop it from disappearing if user clicks outside the box
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setContentView(R.layout.update_dialog) //set the page that will display as a dialog

        updateDialog.etUpdateName.setText(item.name)
        updateDialog.etUpdateBarcode.setText(item.barcode)
        updateDialog.etUpdateQuantity.setText(item.quantity.toString()) // "" +  also would turn it into a string

        updateDialog.tvUpdate.setOnClickListener(View.OnClickListener {

            val name = updateDialog.etUpdateName.text.toString()
            val barcode = updateDialog.etUpdateBarcode.text.toString()
            val quantity = updateDialog.etUpdateQuantity.text.toString().toInt()

            from2 = "0" //tell the db which table to update
            val databaseHandler: DatabaseHandler =
                DatabaseHandler(this) //make an instance of DatabaseHandler

            if (!name.isEmpty() && quantity >= 0) {
                val status =
                    databaseHandler.updateItem(Item(item.id, catCode, name, barcode, quantity))
                if (status > -1) {

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
        })
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

    // Method is used to update the quantity of an item on the shopping list or delete it
    fun updateQuantity(item: Item) {

        val catCode = item.catCode
        val name = item.name
        val barcode = item.barcode
        val quantity = item.quantity

        from2 = "0" // Tell the db to update the shopping list table
        val databaseHandler: DatabaseHandler =
            DatabaseHandler(this) //make an instance of DatabaseHandler

        // Update the item quantity
        if (!name.isEmpty() && quantity >= 0) { // Check quantity has not gone below 0
            val status =
                databaseHandler.updateItem(Item(item.id, catCode, name, barcode, quantity))
            if (status > -1) { // if the sl item quantity has been successfully updated

                // Increment the quantity in pantry
                if (qtyUpdateType0 == "buy1") { // if the user has clicked buy next to a sl item
                    var items = getItemsAny(catCode) // Retrieve the items from the table the item should belong to
                    Toast.makeText(
                        applicationContext, "Catcode: $catCode", Toast.LENGTH_SHORT).show() // testing only
                    var matchingItems = false
                    var itemPantry = item
                    var itemPantryQty = 1000
                    for (i in items)
                        if (i.name == name) { // if item exists in the pantry, increment its quantity
                            matchingItems = true
                            if(itemPantryQty == 1000){
                                i.quantity++
                                itemPantryQty = i.quantity
                                itemPantry = i
                                from2 = i.catCode
                            }
                        }
                    if(matchingItems == true){
                        databaseHandler.updateItem(Item(itemPantry.id, itemPantry.catCode, itemPantry.name,
                            itemPantry.barcode, itemPantry.quantity))
                    }else

                    //TODO remake item in pantry if no longer exists there
                    if(matchingItems == false){
                        Toast.makeText(
                            applicationContext, "Item not found in pantry", Toast.LENGTH_SHORT).show()
                        from2 = catCode
                        databaseHandler.addItem((Item(0, catCode, name, barcode, 1)))
                    }
                }
            }
            setupListofDataIntoRecyclerView()
        // Delete an item
        }else if(quantity < 0){ // if quantity is 0 and user clicks the minus or bin sign again
            item.quantity = 0
            deleteRecordAlertDialog(item)
        }

    }


    // Retrieves Items from the database
    fun getItemsAny(catNo: String): ArrayList<Item> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //variable from2 is in the companion object in the DatabaseHandler.kt class
        from2 = catNo
        //calling the viewItem method of DatabaseHandler class to read the records
        val itemList: ArrayList<Item> = databaseHandler.viewItem()

        return itemList
    }

    // Method is used to show the Add New Item Dialog.  Runs when user picks a category for a new shopping list item

    fun addNewItemDialog() {
        val addItemDialog =
            Dialog(this, R.style.Theme_Dialog) // create a dialog object and set the style
        addItemDialog.setCancelable(false) // prevent the dialog box from closing if user clicks outside of it
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        addItemDialog.setContentView(R.layout.add_item_dialog) // add the page layout to the dialog object

        // set values to default
        addItemDialog.etAddName.setText("")
        addItemDialog.etAddBarcode.setText("0")
        addItemDialog.etAddQty.setText(1.toString()) // "" +  also would turn it into a string

        // when the user has filled in details of the new item and clicks btnAdd (the ADD ITEM button) on the dialog box
        addItemDialog.btnAdd.setOnClickListener {

            // capture the user-decided values
            val name = addItemDialog.etAddName.text.toString()
            val barcode = addItemDialog.etAddBarcode.text.toString()
            val quantity = addItemDialog.etAddQty.text.toString().toInt()
            // check the item doesn't already exist
            val shoppingItems = getItemsShopping()
            var duplicateItemFound = false
            for(i in shoppingItems){
                if(i.name == name && duplicateItemFound == false){
                    duplicateItemFound = true
                    duplicateItemDialog()  //display the duplicate item dialog
                    addItemDialog.dismiss()
                }
            }
            // if no duplicate item exists, continue with adding the new item
            if (duplicateItemFound == false){
                from2 = "0" //tell db which table to update
                val databaseHandler: DatabaseHandler =
                    DatabaseHandler(this) //make an instance of DatabaseHandler

                if (!name.isEmpty() && quantity >= 0) {
                    val status =
                        databaseHandler.addItem((Item(0, catCodeSL, name, barcode, quantity)))
                    if (status > -1) {

                        // create the new item in the chosen category
                        from2 = catCodeSL
                        databaseHandler.addItem((Item(0, catCodeSL, name, barcode, 0)))

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
     * Method is used to show the Alert Dialog
     */
    fun deleteRecordAlertDialog(item: Item) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete SHOPPING LIST item ${item.name}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("YES") { dialogInterface, which ->

            from2 = "0"  //tells db which table item is to be deleted from
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            //calling the deleteItem method of the DatabaseHandler class to delete a record
            //val status = databaseHandler.deleteItem(Item(item.id, "", "0", 1))
            val status = databaseHandler.deleteItem(Item(item.id, item.catCode, item.name, item.barcode, item.quantity))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Item deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()
                setupListofDataIntoRecyclerView()
            } else {Toast.makeText(this, "Item not deleted", Toast.LENGTH_SHORT).show()}

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("NO") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            Toast.makeText(this, "SHOPPING LIST item not deleted", Toast.LENGTH_SHORT).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}
