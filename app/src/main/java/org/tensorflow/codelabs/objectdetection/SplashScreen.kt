package org.tensorflow.codelabs.objectdetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.splash_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.codelabs.objectdetection.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class SplashScreen : AppCompatActivity() {

    private lateinit var image: ImageView
    lateinit var dbHelper: DbHelper
    private var isInternetOn = false
    private var countLaunchApp: Int = 0

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        val foodAnimation = AnimationUtils.loadAnimation(this, R.anim.food_animation)
        val counterAnimation = AnimationUtils.loadAnimation(this, R.anim.counter_animation)

        food_txt_intro.startAnimation(foodAnimation)
        counter_txt_intro.startAnimation(counterAnimation)

        supportActionBar!!.hide()

        dbHelper = DbHelper(this)
        image = findViewById(R.id.pizzaId)

        CoroutineScope(Dispatchers.IO).launch {
            animatePizza() // с помощью корутины отдельно анимируем пиццу
        }

        countLaunchApp = loadData()

        // получили кол-во раз запуска приложения. если равен 1, то
        // загружаем данные из сервера в бд sqlite
        if (countLaunchApp == 1) {
            println("ЗАГРУЖАЕМ ДАННЫЕ С СЕРВЕРА, КОЛ-ВО ЗАПУЩЕННЫХ РАЗ = $countLaunchApp")

            // Проверка интернет соединения
            val connectionManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectionManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                isInternetOn = true
                println("Network Available")
                lifecycleScope.launch(Dispatchers.Default) { getServerDataInDB() }
            } else {
                println("Network NOT Available")
                val view = View.inflate(this, R.layout.dialog_view, null)

                val builder = AlertDialog.Builder(this)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
            }
        }
        if (countLaunchApp >= 2) {
            countLaunchApp += 1
            println("ЗАПУСК НЕ ПЕРВЫЙ, КОЛ-ВО ЗАПУЩЕННЫХ РАЗ = $countLaunchApp")
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        saveData(countLaunchApp)
    }

    private fun getServerDataInDB() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://peaceful-garden-62887.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ProductController = retrofit.create(ProductController::class.java)

        val call: Call<MutableList<ServerFood>> = service.get()
        call.enqueue(object : Callback<MutableList<ServerFood>> {
            override fun onResponse(
                call: Call<MutableList<ServerFood>>, response: Response<MutableList<ServerFood>>
            ) {
                if (response.isSuccessful) {
                    val food: MutableList<ServerFood>? = response.body()
                    if (food != null) {
                        Log.d("Server Response", "Getting server data is successful!")
                        for (item in food) {
                            try {
                                dbHelper.insert(
                                    item.product.toString(),
                                    item.image_url.toString(),
                                    item.proteins.toString(),
                                    item.fats.toString(),
                                    item.carbs.toString(),
                                    item.kcal.toString()
                                )
                            } catch (e: Exception) {
                                Log.d("SQLite exception", "Adding dublicates in DB")
                            }
                        }
                        countLaunchApp += 1
                        saveData(countLaunchApp)
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    }
                } else Log.d("Server Response", "Response is not successful")
            }

            override fun onFailure(call: Call<MutableList<ServerFood>>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    getServerDataInDB()
                    Log.d(
                        "Server Response",
                        "Socket Time out. Please try again. Server is sleeping :("
                    )
                } else Log.d("Server Response", "Server error: " + t.message)
            }
        })
    }


    private fun saveData(count: Int) { // сохранение кол-ва запуска приложения
        val sharedPreferences = getSharedPreferences("LaunchesCount", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putInt("COUNT_KEY", count)
        }.apply()

    }

    private fun loadData(): Int { // получение кол-ва запука приложения
        val sharedPreferences = getSharedPreferences("LaunchesCount", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("COUNT_KEY", 1)

    }

    private fun animatePizza() { // анимация пиццы
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        image.animation = rotate

    }
}