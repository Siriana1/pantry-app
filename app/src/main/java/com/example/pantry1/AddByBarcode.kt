package com.example.pantry1
/*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sqlitedemo.DatabaseHandler
import kotlinx.android.synthetic.main.activity_bread1.*
import kotlinx.android.synthetic.main.add_item.*
import kotlinx.android.synthetic.main.add_item.btnAdd
import kotlinx.android.synthetic.main.add_item.etBarcode
import kotlinx.android.synthetic.main.add_item.etItem
import kotlinx.android.synthetic.main.add_item.etQuantity

class AddByBarcode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_by_barcode)

        //method for saving records in database by barcode
        fun addItemByBarcode(view: View) {
            val name = etItem.text.toString()
            //val barcode = barcode
            val quantity = etQuantity.text.toString().toInt()
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (!name.isEmpty()) {
                val status =
                    databaseHandler.addItem(Item(0, name, barcode, quantity))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Item successfully added!", Toast.LENGTH_LONG).show()
                    etItem.text.clear()
                    etBarcode.setText("0") //needs set to 0
                    etQuantity.setText("1") //needs set to 1

                    btnAdd.visibility = View.GONE
                    llQtyAdd.visibility = View.GONE
                    llBarcodeAdd.visibility = View.GONE
                    llItemAdd.visibility = View.GONE

                    var bread1 = Bread()
                    bread1.setupListofDataIntoRecyclerView()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name cannot be blank and quantity must be a positive number",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
}*/

