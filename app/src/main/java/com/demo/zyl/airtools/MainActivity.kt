package com.demo.zyl.airtools

import android.os.Bundle
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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadData(CommonConsts.URL_AIR_NEWS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                loadData(CommonConsts.URL_CTRIP_AIR_TICKET)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                loadData(CommonConsts.URL_CHIP_AIR_TICKET)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun  loadData(url: String) {
        needClearHistory = true
//        content?.clearCache(true)
        var settings: WebSettings = content!!.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        initWebViewClient()
        initWebViewBackListener()
        content?.loadUrl(url)
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
                content?.loadUrl(url)
                return false
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

        loadData(CommonConsts.URL_AIR_NEWS)
    }

    private fun initEvent() {
        navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
