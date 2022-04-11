package com.example.checkweather
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.checkweather.dialog.view.LocationDialog

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    lateinit var firstOpen:String;
    lateinit var lat:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


       getSharedPreferences(this).apply {
                firstOpen = getString(getString(R.string.firstOpen), "true")!!
            }
        getSharedPreferences(this).apply {
            lat = getString(getString(R.string.lat), "")!!
        }
        Handler().postDelayed({
            if(firstOpen.equals("true")){
                val intent = Intent(applicationContext, LocationDialog::class.java)
                startActivity(intent)
            }
            else
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }, 4000)

    }
}