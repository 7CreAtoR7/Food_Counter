package org.tensorflow.codelabs.objectdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.language_layout.*

class Ingredients : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerIngredients.ViewHolder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)


        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerIngredients()
        recyclerView.adapter = adapter

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.title = getString(R.string.ingredients_txt)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        MainActivity.ingList.clear()
        super.onDestroy()
    }
}
