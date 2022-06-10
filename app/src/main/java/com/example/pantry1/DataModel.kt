package com.example.pantry1

// This class is for when there is more than one type of ViewHolder in the same recycler view

data class DataModel (val anItem:Item, val viewType: Int) {
}