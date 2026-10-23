package com.example.newsandactions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.newsandactions.Retrofit.NewsRetrofit
import com.example.newsandactions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retro: NewsRetrofit

    companion object SaveMe2 {
        lateinit var newsCtx: Context

        var latestNewsLayoutRestore: LinearLayout? = null
        var sportNewsLayoutRestore: LinearLayout? = null
        var economyNewsLayoutRestore: LinearLayout? = null
        var politicsNewsLayoutRestore: LinearLayout? = null
        var worldNewsLayoutRestore: LinearLayout? = null
        var actionsNewsLayoutRestore: LinearLayout? = null

        var current: String = "Últimas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Notícias e Ações"

        current = "Últimas"
        val nFrag = NewsFragmentActivity()
        replaceFragment(nFrag)

        // Show loading initially ("é feito loading na app")
        binding.loadingLayout.visibility = View.VISIBLE

        retro = NewsRetrofit(this)
        retro.getLatest()

        binding.latest.setOnClickListener {
            current = "Últimas"
            replaceFragment(NewsFragmentActivity())
        }
        binding.sports.setOnClickListener {
            current = "Desporto"
            replaceFragment(NewsFragmentActivity())
        }
        binding.economics.setOnClickListener {
            current = "Economia"
            replaceFragment(NewsFragmentActivity())
        }
        binding.politics.setOnClickListener {
            current = "Política"
            replaceFragment(NewsFragmentActivity())
        }
        binding.world.setOnClickListener {
            current = "Mundo"
            replaceFragment(NewsFragmentActivity())
        }
        binding.actions.setOnClickListener {
            current = "Ações"
            replaceFragment(NewsFragmentActivity())
        }
    }

    fun setUpRetro() {
        retro.getByCat("Desporto")
        retro.getByCat("Economia")
        retro.getByCat("Política")
        retro.getByCat("Mundo")
        retro.getByCat("Ações")
    }

    fun hideLoading() {
        binding.loadingLayout.visibility = View.GONE
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragTransaction = fragmentManager.beginTransaction()
        fragTransaction.replace(R.id.fragmentContainerView, fragment)
        fragTransaction.commit()
    }
}
