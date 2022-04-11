package com.example.checkweather
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.checkweather.alert.view.AlertFragment
import com.example.checkweather.databinding.ActivityMainBinding
import com.example.checkweather.favorites.view.FavoriteFragment
import com.example.checkweather.home.view.HomeFragment
import com.example.checkweather.setting.SettingFragment
import java.util.*
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        getSharedPreferences(this).apply {
            language = getString(getString(R.string.languageSettings), "en")!!
        }
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.getItemId()) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.favorites -> {
                    replaceFragment(FavoriteFragment())
                }

                R.id.alerts ->{
                    replaceFragment(AlertFragment())
                }
                R.id.settings ->{
                    replaceFragment(SettingFragment())
                }
            }
            true
        }

    }

    override fun onResume() {
        super.onResume()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }
}