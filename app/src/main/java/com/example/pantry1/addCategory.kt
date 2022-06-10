package com.example.pantry1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_category.*

class addCategory : AppCompatActivity() {

    //view ids from the related xml file
    //val home_btn = findViewById<Button>(R.id.home_btn)
    //val enter_new_category_name_et = findViewById<EditText>(R.id.enter_new_category_name_et)
    //val submit_btn = findViewById<Button>(R.id.submit_btn)

    //methods of class addCategory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        //for when the submit button is clicked - part 2/3 of the collaboration
        submit_btn.setOnClickListener {
            val intent = Intent() //create an empty intent so we can pass information to it
            intent.putExtra(Pantry.CATNAME, enter_new_category_name_et.text.toString()) //map - put the value the user
            // has typed into enter_new_category_name_et into the CATNAME variable from MainActivity
            setResult(Activity.RESULT_OK, intent) //set the resultCode and include the intent created above, and send
            // them both to onActivityResult() in MainActivity.kt
            finish()
        }
        //for when the home button is clicked - part 2/3
        val home_btn = findViewById<Button>(R.id.home_btn)
        home_btn.setOnClickListener {
            Toast.makeText(this, "Category was not added", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}