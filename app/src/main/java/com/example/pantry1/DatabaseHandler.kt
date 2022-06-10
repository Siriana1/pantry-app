package com.sqlitedemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.pantry1.Item
import com.example.pantry1.ShoppingList

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ItemDatabase8" //change this name if you need to add more tables
        private val TABLE_CONTACTS = "ItemTable"
        private val TABLE_BAKING = "BakingTable"
        private val TABLE_SHOPPING = "ShoppingTable"

        private val KEY_ID = "_id"
        private val KEY_CATCODE = "catCode"
        private val KEY_NAME = "name"
        private val KEY_BARCODE = "barcode"
        private val KEY_QUANTITY = "quantity"

        var from2 = ""
    }
    //Instance variables
    var selectQuery = "" //created here to make it global
    var success: Long = 0  //created here to make it global
    var successU = 0
    var successD = -1

    //Methods

    //--------------------START OF ONCREATE---------------------------------------------------------------------

    //Method to create a new table in a database
    override fun onCreate(db: SQLiteDatabase?) { //argument is a database
        //creating a table to go in db called TABLE_CONTACTS with columns KEY_ID, KEY_NAME, KEY_BARCODE and KEY_QUANTITY
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATCODE + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_BARCODE + " TEXT," + KEY_QUANTITY + " INTEGER" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE) //create the table in db

        //creating a table to go in db called TABLE_BAKING with columns KEY_ID, KEY_NAME, KEY_BARCODE and KEY_QUANTITY
        val CREATE_BAKING_TABLE = ("CREATE TABLE " + TABLE_BAKING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATCODE + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_BARCODE + " TEXT," + KEY_QUANTITY + " INTEGER" + ")")
        db?.execSQL(CREATE_BAKING_TABLE) //create the table in db

        //creating a table to go in db called TABLE_BAKING with columns KEY_ID, KEY_NAME, KEY_BARCODE and KEY_QUANTITY
        val CREATE_SHOPPING_TABLE = ("CREATE TABLE " + TABLE_SHOPPING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATCODE + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_BARCODE + " TEXT," + KEY_QUANTITY + " INTEGER" + ")")
        db?.execSQL(CREATE_SHOPPING_TABLE) //create the table in db
    }

    //--------------------END OF ONCREATE---------------------------------------------------------------------


    //Function creates a new version number for an updated table
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAKING)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING)

        onCreate(db)
    }

    /**
     * Function to insert data
     */
    fun addItem(item: Item): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_CATCODE, item.catCode)
        contentValues.put(KEY_NAME, item.name) // Item class name
        contentValues.put(KEY_BARCODE, item.barcode)
        contentValues.put(KEY_QUANTITY, item.quantity)

        // Inserting Row
        if(from2 == "2"){ //baking
             success = db.insert(TABLE_BAKING, null, contentValues)
        }else if (from2 == "1"){ //bread
            success = db.insert(TABLE_CONTACTS, null, contentValues)
        }else if (from2 == "0") { //shopping
            success = db.insert(TABLE_SHOPPING, null, contentValues)
            //2nd argument is String containing nullColumnHack - null means the data is to be saved locally
            db.close() // Closing database connection
        }
        return success
    }

    // method to read data
    fun viewItem(): ArrayList<Item> {

        val Itemlist: ArrayList<Item> = ArrayList<Item>()

        if(from2 == "2"){
            selectQuery = "SELECT  * FROM $TABLE_BAKING ORDER BY $KEY_NAME"  //baking
        }else if (from2 == "1"){
            selectQuery = "SELECT  * FROM $TABLE_CONTACTS ORDER BY $KEY_NAME" //bread
        }else if (from2 == "0"){
            selectQuery = "SELECT  * FROM $TABLE_SHOPPING ORDER BY $KEY_NAME" //shopping list
        }

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var catCode: String
        var name: String
        var barcode: String
        var quantity: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                catCode = cursor.getString(cursor.getColumnIndex(KEY_CATCODE))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                barcode = cursor.getString(cursor.getColumnIndex(KEY_BARCODE))
                quantity = cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY))

                val item = Item(id = id, catCode = catCode, name = name, barcode = barcode, quantity = quantity)
                Itemlist.add(item)

            } while (cursor.moveToNext())
        }
        return Itemlist
    }

    /**
     * Function to update record
     */
    fun updateItem(item: Item): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CATCODE, item.catCode)
        contentValues.put(KEY_NAME, item.name)
        contentValues.put(KEY_BARCODE, item.barcode)
        contentValues.put(KEY_QUANTITY, item.quantity)

        // Updating Row
        if (from2 == "1"){  //bread
            successU = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + item.id, null)
            //2nd argument is String containing nullColumnHack
        }else if(from2 == "2"){ //baking
            successU = db.update(TABLE_BAKING, contentValues, KEY_ID + "=" + item.id, null)
            //2nd argument is String containing nullColumnHack
        }
        else if(from2 == "0"){ //shopping list
            successU = db.update(TABLE_SHOPPING, contentValues, KEY_ID + "=" + item.id, null)
            //2nd argument is String containing nullColumnHack
        }

        db.close() // Closing database connection
        return successU
    }

    /**
     * Function to delete record
     */
    fun deleteItem(item: Item): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, item.id)
        // Deleting Row
        if (from2 == "1") { //bread
            successD = db.delete(TABLE_CONTACTS, KEY_ID + "=" + item.id, null)
        }else if (from2 == "2"){ //baking
            successD = db.delete(TABLE_BAKING, KEY_ID + "=" + item.id, null)
        }else if (from2 == "0") { //shopping list
            successD = db.delete(TABLE_SHOPPING, KEY_ID + "=" + item.id, null)
        }

        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return successD
    }
}