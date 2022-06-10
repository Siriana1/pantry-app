package com.example.pantry1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cat_picker_dialog.*

// This class shows a picker so the user can choose a category for their new shopping list item
class CatPickerDialog : AppCompatActivity() {

    //-------------------------- Start of on create ---------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat_picker_dialog)

        // On click events for each category in the picker

        tvBaking.setOnClickListener {
            val intent = Intent(this, ShoppingList::class.java)
            intent.putExtra("CATCODESL", "2")
            intent.putExtra("FROM", "cat picker")
            startActivity(intent)
            finish()
        }

    }
    //___________________________________ End of on create __________________________________________________________
}