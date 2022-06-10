package com.example.pantry1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pantry1.Baking.Companion.qtyUpdateType2
import com.example.pantry1.Bread.Companion.qtyUpdateType1
import com.example.pantry1.ShoppingList.Companion.qtyUpdateType0
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.item_row_sl.view.ivBuy1Sl
import kotlinx.android.synthetic.main.items_row.view.ivIncrement
import kotlinx.android.synthetic.main.items_row.view.tvName
import kotlinx.android.synthetic.main.items_row.view.tvQuantity
import kotlinx.android.synthetic.main.item_row_sl.view.*

//class ItemAdapter(val context: Context, val items: ArrayList<Item>) :
//RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
class ItemAdapter(val context: Context, val items: ArrayList<DataModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE1 = 1
        const val VIEW_TYPE2 = 2
    }

    /**
     * Inflates the item views which are designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE1) { // if the rv is on ShoppingList
            return ViewHolderSl(
                LayoutInflater.from(context).inflate(
                    R.layout.item_row_sl,
                    parent,
                    false
                )
            )
        } else{
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.items_row,
                    parent,
                    false
                )
            )
        }
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */

    // override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = items.get(position) //get a single item from the list - 1 item per vh

        if(holder is ViewHolderSl){ //added with DataModel
            holder.tvNameSl?.text = item.anItem.name  // here item is a DataModel
            //holder.tvBarcodeSl.text = item.anItem.barcode
            holder.tvQuantitySl?.text = item.anItem.quantity.toString() //this needs to be an Int

            // Updating the background color according to the odd/even positions in list.
            if (position % 2 == 0) {
                holder.llShoppingList.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorLightGray
                    )
                )
            } else {
                holder.llShoppingList.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
            }
            // User presses the plus sign next to an item on the shopping list
            holder.ivIncrementSl.setOnClickListener {

                if (context is ShoppingList) {
                    qtyUpdateType0 = "increment"
                    item.anItem.quantity++
                    context.updateQuantity(item.anItem) // here context is acting as an instance of SL
                }
            }
            // User presses the minus sign next to an item on the shopping list - this will either decrement the quantity
            // or give an option to delete if the quantity drops below zero
            holder.ivDeleteSl.setOnClickListener {

                if (context is ShoppingList) {
                    qtyUpdateType0 = "delete"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem)
                }
            }
            // on click event for the buy button next to a sl item
            holder.ivBuy1Sl.setOnClickListener {
                if (context is ShoppingList) {
                    qtyUpdateType0 = "buy1"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem)
                }
            }
            // on click event for long pressing the name of a shopping list item
            holder.tvNameSl!!.setOnLongClickListener() {

                if (context is ShoppingList) { //if on the Shopping page
                    context.updateRecordDialog(item.anItem) // pass in the item on that row
                }
                true
            }

        }else if(holder is ViewHolder) {
            holder.tvName?.text = item.anItem.name
            //holder.tvBarcode.text = item.anItem.barcode
            holder.tvQuantity?.text = item.anItem.quantity.toString() //this needs to be an Int

            // Updating the background color according to the odd/even positions in list.
            if (position % 2 == 0) {
                holder.llMain?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorLightGray
                    )
                )
            } else {
                holder.llMain?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
            }
            holder.ivEdit.setOnClickListener { view ->

                if (context is Bread) { //if on the Bread page
                    context.updateRecordDialog(item.anItem) // pass in the item on that row
                } else if (context is Baking) {
                    context.updateRecordDialog(item.anItem)
                }
            }
            // on click event for long pressing the name of an item
            holder.tvName!!.setOnLongClickListener() {

                if (context is Baking) { //if on the Baking page
                    context.updateRecordDialog(item.anItem) // pass in the item on that row
                } else if (context is Bread) {
                    context.updateRecordDialog(item.anItem)
                }
                true
            }
            holder.ivIncrement.setOnClickListener { view ->

                if (context is Bread) {  // if we are on the Bread page
                    qtyUpdateType1 = "increment"
                    item.anItem.quantity++
                    context.updateQuantity(item.anItem) // here context is in essence, acting as an instance of Bread
                } else if (context is Baking) {
                    qtyUpdateType2 = "increment"
                    item.anItem.quantity++
                    context.updateQuantity(item.anItem)
                }
            }
            holder.ivEaten.setOnClickListener { view ->

                if (context is Bread) { //if on the Bread page
                    qtyUpdateType1 = "moveToSL"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem) // here context is in essence, acting as an instance of Bread
                } else if (context is Baking) {
                    qtyUpdateType2 = "moveToSL"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem) // here context is in essence, acting as an instance of Bread
                }
            }
            holder.ivThrowout.setOnClickListener { view ->

                if (context is Bread) {  // if we are on the Bread page
                    qtyUpdateType1 = "throw out"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem) // here context is in essence, acting as an instance of Bread
                } else if (context is Baking) {
                    qtyUpdateType2 = "throw out"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem)
                } else if (context is ShoppingList) {
                    qtyUpdateType0 = "decrement"
                    item.anItem.quantity--
                    context.updateQuantity(item.anItem)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to - i think that here we are taking the views
        // from items_row and putting them in holders

        // from items_row
        val llMain: LinearLayout? = view.llMain  // changed from val llmain = view.llMain
        val tvName: TextView? = view.tvName
        val ivEdit: ImageView = view.ivEdit  //hidden
        val tvQuantity: TextView? = view.tvQuantity
        val ivIncrement: ImageView = view.ivIncrement
        val ivEaten: ImageView = view.ivEaten
        val ivThrowout: ImageView = view.ivThrowout
    }

    class ViewHolderSl(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to - i think that here we are taking the views
        // from item_row_sl.xml.xml and putting them in the holder

        val llShoppingList: LinearLayout = view.llShoppingList
        val tvNameSl: TextView? = view.tvNameSl
        val tvQuantitySl: TextView? = view.tvQuantitySl
        val ivIncrementSl: ImageView = view.ivIncrementSl
        val ivDeleteSl: ImageView = view.ivDeleteSl
        val ivBuy1Sl: ImageView = view.ivBuy1Sl
    }
}