package com.hanova

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanova.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var dumantext: TextView
    private lateinit var harekettext: TextView
    private lateinit var otoparktext: TextView

    private val handler = Handler()
    private val updateTask = object : Runnable {
        override fun run() {
            fetchData() // Fetch temperature and humidity data
            fetchMotionStatus() // Fetch motion sensor status
            fetchPiezoStatus() // Fetch piezo sensor status
            handler.postDelayed(this, 5000) // Update every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hide ActionBar

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable immersive mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Fullscreen mode
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
                        or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        tvTemperature = findViewById(R.id.tvTemperature)
        tvHumidity = findViewById(R.id.tvHumidity)
        dumantext = findViewById(R.id.dumantext)
        harekettext = findViewById(R.id.harekettext)
        otoparktext = findViewById(R.id.otoparktext)

        handler.post(updateTask) // Start data fetching

        val button11 = findViewById<MaterialButton>(R.id.button11)
        val button10 = findViewById<MaterialButton>(R.id.button10)

        button11.setOnClickListener {
            sendHttpRequest("http://192.168.4.1/led?state=2")
        }

        button10.setOnClickListener {
            sendHttpRequest("http://192.168.4.1/led?state=1")
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTask) // Continue fetching when app resumes
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTask) // Stop fetching when app pauses
    }

    private fun fetchData() {
        val url = "http://192.168.4.1/data"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    val jsonObject = JSONObject(jsonResponse)
                    val temperature = jsonObject.optDouble("temperature", Double.NaN)
                    val humidity = jsonObject.optDouble("humidity", Double.NaN)
                    val gasStatus = jsonObject.optString("status", "Karbondioksit Algilanmadi")

                    runOnUiThread {
                        if (!temperature.isNaN()) tvTemperature.text = "${temperature.toInt()}°C"
                        if (!humidity.isNaN()) tvHumidity.text = "${humidity.toInt()}%"
                        dumantext.text = gasStatus
                    }
                }
            }
        })
    }
    private fun fetchDumanStatus() {
        val url = "http://192.168.4.1/mq2"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    val jsonObject = JSONObject(jsonResponse)
                    val gasStatus = jsonObject.optString("status", "Karbondioksit Algilanmadi")

                    runOnUiThread {
                        dumantext.text = gasStatus
                    }
                }
            }
        })
    }
    private fun fetchMotionStatus() {
        val url = "http://192.168.4.1/pir"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    val jsonObject = JSONObject(jsonResponse)
                    val message = jsonObject.optString("message", "No data")

                    runOnUiThread {
                        harekettext.text = message
                    }
                }
            }
        })
    }

    private fun fetchPiezoStatus() {
        val url = "http://192.168.4.1/piezo"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            private fun fetchPiezoStatus() {
                val url = "http://192.168.4.1/piezo"

                val request = Request.Builder().url(url).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val jsonResponse = response.body?.string()
                        if (jsonResponse != null) {
                            val jsonObject = JSONObject(jsonResponse)
                            val message = jsonObject.optString("message", "No data")

                            runOnUiThread {
                                otoparktext.text = message
                            }
                        }
                    }
                })
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    val jsonObject = JSONObject(jsonResponse)
                    val message = jsonObject.optString("message", "No data")

                    runOnUiThread {
                        otoparktext.text = message
                    }
                }
            }
        })
    }

    private fun sendHttpRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: "No Response"
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Response: $responseBody", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
