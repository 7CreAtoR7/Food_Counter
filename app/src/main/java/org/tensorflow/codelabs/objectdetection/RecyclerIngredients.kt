package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerIngredients : RecyclerView.Adapter<RecyclerIngredients.ViewHolder>() {

    lateinit var context: Context
    private lateinit var dbHelper: DbHelper
    private lateinit var myPreference: MyPreference

    private var titles: ArrayList<String> = ArrayList()
    private var images: ArrayList<String> = ArrayList()
    private var imagesNotInternet = arrayOf(
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet,
        R.drawable.dish_not_internet
    )
    private var proteins: ArrayList<String> = ArrayList()
    private var fats: ArrayList<String> = ArrayList()
    private var carbs: ArrayList<String> = ArrayList()
    private var kcal: ArrayList<String> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerIngredients.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return MainActivity.ingList.distinct().size // передаем кол-во распознанных объектов
    }


    override fun onBindViewHolder(holder: RecyclerIngredients.ViewHolder, position: Int) {
        val array = MainActivity.ingList.distinct()

        // словарь продуктов, где их значения - словари с языками
        val productsInLanguages = mapOf(
            "Tomato" to mapOf("ru" to "Помидор", "en" to "Tomato", "hsb" to "Tomate", "ja" to "トマト"),
            "Cheese" to mapOf("ru" to "Сыр", "en" to "Cheese", "hsb" to "Käse", "ja" to "チーズ")
        )

        myPreference = MyPreference(holder.itemTitle.context)
        dbHelper = DbHelper(holder.itemImage.context)
        val lang = myPreference.getLoginCount()

        // используя словарь находим слово в конкретном языке и вставляем в titles
        for (product in array) {
            val name = productsInLanguages[product]?.get(lang)
            if (name != null) titles.add(name) else titles.add(product)
            images.add(dbHelper.selectImgAboutProduct(product))
            proteins.add(dbHelper.selectProteinsAboutProduct(product))
            fats.add(dbHelper.selectFatsAboutProduct(product))
            carbs.add(dbHelper.selectCarbsAboutProduct(product))
            kcal.add(dbHelper.selectKcalAboutProduct(product))
        }

        holder.itemTitle.text = titles[position]
        holder.itemImage.setImageResource(imagesNotInternet[position])
        holder.itemProteins.text = proteins[position]
        holder.itemFats.text = fats[position]
        holder.itemCarbs.text = carbs[position]
        holder.itemKcal.text = kcal[position]

        try {
            Picasso.with(holder.itemImage.context).load(images[position]).resize(600, 600)
                .into(holder.itemImage)
        } catch (e: Exception) {
            holder.itemImage.setImageResource(imagesNotInternet[position])
        }
        // если добавлено пустое фото, то вставляем шаблонное
        if (holder.itemImage.drawable == null) {
            holder.itemImage.setImageResource(imagesNotInternet[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.im_view)
        var itemTitle: TextView = itemView.findViewById(R.id.product_txt)
        var itemProteins: TextView = itemView.findViewById(R.id.proteins_info)
        var itemFats: TextView = itemView.findViewById(R.id.fats_txt)
        var itemCarbs: TextView = itemView.findViewById(R.id.carbs_info)
        var itemKcal: TextView = itemView.findViewById(R.id.kcal_info)

        init {
            itemView.setOnClickListener {
                adapterPosition
            }
        }
    }
}