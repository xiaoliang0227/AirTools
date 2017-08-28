package com.demo.zyl.airtools

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {

    private var content: WebView? = null

    private var navigation: BottomNavigationView? = null

    private var needClearHistory: Boolean = false

    private var handler: Handler = Handler()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadData(CommonConsts.URL_AIR_NEWS, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                loadData(CommonConsts.URL_CTRIP_AIR_TICKET, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                loadData(CommonConsts.URL_CHIP_AIR_TICKET, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun  loadData(url: String, needDelay: Boolean) {
        loadBlankPage()
        delayLoadPage(url, needDelay)
    }

    private fun  delayLoadPage(url: String, needDelay: Boolean) {
        val duration:Long = if (needDelay) 100 else 0
        handler.postDelayed({
            needClearHistory = true
            var settings: WebSettings = content!!.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            settings.setAppCacheEnabled(true)
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            if (isNetworkAvailable()) {
                settings.cacheMode = WebSettings.LOAD_DEFAULT
            } else {
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }

            initWebViewClient()
            initWebViewBackListener()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
            content?.loadUrl(url)
        }, duration)
    }

    private fun loadBlankPage() {
        content?.loadUrl("about:blank")
    }

    private fun isNetworkAvailable(): Boolean {
        var cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info: NetworkInfo = cm.activeNetworkInfo
        return info != null && info.isAvailable
    }

    private fun initWebViewBackListener() {
        content?.setOnKeyListener(object :View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && content!!.canGoBack()) {
                        content?.goBack()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun initWebViewClient() {
        content?.setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (!url!!.startsWith("ctrip:")) {
                    content?.loadUrl(url)
                }
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                if (needClearHistory) {
                    needClearHistory = false
                    content?.clearHistory()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        initField()
        initEvent()
    }

    private fun initField() {
        navigation = findViewById(R.id.navigation) as BottomNavigationView
        content = findViewById(R.id.content) as WebView

        loadData(CommonConsts.URL_AIR_NEWS, false)
    }

    private fun initEvent() {
        navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
