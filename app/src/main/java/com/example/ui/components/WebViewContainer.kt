package com.example.ui.components

import android.graphics.Bitmap
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.UiEvent
import com.example.ui.WebViewState
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun WebViewContainer(
    state: WebViewState,
    uiEvents: SharedFlow<UiEvent>,
    onNavigationStateChanged: (url: String, canGoBack: Boolean, canGoForward: Boolean, title: String?) -> Unit,
    onProgressChanged: (progress: Int, isLoading: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }
    }

    // Handle User-Agent and Desktop Mode updates
    LaunchedEffect(state.activeUserAgent, state.isDesktopMode, state.customUserAgentString) {
        val baseUa = if (state.activeUserAgent.id == "ua_custom" && state.customUserAgentString.isNotBlank()) {
            state.customUserAgentString
        } else {
            state.activeUserAgent.userAgent
        }

        val finalUa = if (state.isDesktopMode) {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
        } else {
            baseUa
        }

        if (webView.settings.userAgentString != finalUa) {
            webView.settings.userAgentString = finalUa
            webView.reload()
        }
    }

    // Set up Clients
    LaunchedEffect(webView) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                // Keep all facebook and web links inside webview
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                onProgressChanged(10, true)
                if (url != null && view != null) {
                    onNavigationStateChanged(
                        url,
                        view.canGoBack(),
                        view.canGoForward(),
                        view.title
                    )
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onProgressChanged(100, false)
                if (url != null && view != null) {
                    onNavigationStateChanged(
                        url,
                        view.canGoBack(),
                        view.canGoForward(),
                        view.title
                    )
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress, newProgress < 100)
                if (view != null && view.url != null) {
                    onNavigationStateChanged(
                        view.url!!,
                        view.canGoBack(),
                        view.canGoForward(),
                        view.title
                    )
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (view != null && view.url != null) {
                    onNavigationStateChanged(
                        view.url!!,
                        view.canGoBack(),
                        view.canGoForward(),
                        title
                    )
                }
            }
        }
    }

    // Handle UI Events from ViewModel
    LaunchedEffect(uiEvents) {
        uiEvents.collect { event ->
            when (event) {
                is UiEvent.LoadUrl -> {
                    webView.loadUrl(event.url)
                }
                is UiEvent.ReloadWebView -> {
                    webView.reload()
                }
                is UiEvent.ClearWebViewData -> {
                    webView.clearCache(true)
                    webView.clearHistory()
                    webView.reload()
                }
                is UiEvent.GoBack -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                }
                is UiEvent.GoForward -> {
                    if (webView.canGoForward()) {
                        webView.goForward()
                    }
                }
                else -> {}
            }
        }
    }

    // Initial URL load
    LaunchedEffect(Unit) {
        if (webView.url == null || webView.url != state.currentUrl) {
            webView.loadUrl(state.currentUrl)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.destroy()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
