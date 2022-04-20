package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.language_layout.*


class Language : AppCompatActivity() {
    private lateinit var myPreference: MyPreference
    lateinit var context: Context
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_layout)

        context = this
        myPreference = MyPreference(this)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = getString(R.string.choose_language)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }
}