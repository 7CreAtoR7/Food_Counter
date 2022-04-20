package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    companion object {
        @JvmField
        var langNow: String = ""
    }

    private lateinit var myPreference: MyPreference
    lateinit var context: Context

    // инициализация массивов с флагами и названиями языков
    private var titles = arrayOf("Русский", "English", "Deutsche", "日本")
    private var images = arrayOf(
        R.drawable.russia_flag_,
        R.drawable.united_states_flag_,
        R.drawable.germany,
        R.drawable.japan
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return titles.size
    }


    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemImage.setImageResource(images[position])

        holder.itemView.setOnClickListener {
            myPreference = MyPreference(holder.itemTitle.context)

            when {
                holder.itemTitle.text as String == "Русский" -> {
                    myPreference.setLoginCount("ru") // название языка (Русский, English) передаем в sp
                    val context = holder.itemTitle.context
                    val intent = Intent(context, MainActivity::class.java)
                    langNow = "ru"
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
                holder.itemTitle.text as String == "English" -> {
                    myPreference.setLoginCount("en") // название языка (Русский, English) передаем в sp
                    val context = holder.itemTitle.context
                    val intent = Intent(context, MainActivity::class.java)
                    langNow = "en"
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
                holder.itemTitle.text as String == "Deutsche" -> {
                    myPreference.setLoginCount("hsb") // название языка (Русский, English) передаем в sp
                    val context = holder.itemTitle.context
                    val intent = Intent(context, MainActivity::class.java)
                    langNow = "hsb"
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
                else -> {
                    myPreference.setLoginCount("ja") // название языка (Русский, English) передаем в sp
                    val context = holder.itemTitle.context
                    val intent = Intent(context, MainActivity::class.java)
                    langNow = "ja"
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)

        init {

            itemView.setOnClickListener {
                adapterPosition

            }
        }
    }
}