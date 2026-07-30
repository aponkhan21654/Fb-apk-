package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Presets
import com.example.model.UserAgentItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val DEFAULT_FACEBOOK_URL = "https://m.facebook.com/mreg?e_token=AbkdgkgPLqQ_xmavX1koYXq51xZkP-95Wq96iKw67-6q_CMRimxVmyI8Pa-8jYyE-h7bd9GTcnnylw&d_hash=FBA71FDC8239E90131BC4314E9B4E92E&cid=256002347743983&app_versio"

data class WebViewState(
    val currentUrl: String = DEFAULT_FACEBOOK_URL,
    val savedPassword: String = "",
    val activeUserAgentId: String = "ua_chrome_android",
    val activeUserAgent: UserAgentItem = Presets.DEFAULT_USER_AGENTS.first(),
    val customUserAgentString: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val pageTitle: String = "Facebook",
    val isDesktopMode: Boolean = false,
    val userAgentList: List<UserAgentItem> = Presets.DEFAULT_USER_AGENTS,
    val showUaSheet: Boolean = false,
    val showCustomUaDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showTelegramPopup: Boolean = false
)

object AppSecurity {
    // Obfuscated Base64 representation of "https://t.me/TeamWithApon"
    private const val B64_TG_URL = "aHR0cHM6Ly90Lm1lL1RlYW1XaXRoQXBvbg=="

    fun getTelegramUrl(): String {
        return try {
            val bytes = android.util.Base64.decode(B64_TG_URL, android.util.Base64.DEFAULT)
            String(bytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "https://t.me/TeamWithApon"
        }
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object ReloadWebView : UiEvent()
    object ClearWebViewData : UiEvent()
    data class LoadUrl(val url: String) : UiEvent()
    object GoBack : UiEvent()
    object GoForward : UiEvent()
    data class EvaluateJavascript(val script: String) : UiEvent()
}

class TokenViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("token_switcher_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(WebViewState())
    val uiState: StateFlow<WebViewState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        val lastUaId = prefs.getString("last_ua_id", "ua_chrome_android")
        val customUaString = prefs.getString("custom_ua_string", "") ?: ""
        val savedPwd = prefs.getString("saved_fb_password", "") ?: ""
        val activeUa = Presets.DEFAULT_USER_AGENTS.find { it.id == lastUaId }
            ?: Presets.DEFAULT_USER_AGENTS.first()

        val isTelegramShown = prefs.getBoolean("telegram_popup_shown_v1", false)

        _uiState.update {
            it.copy(
                currentUrl = DEFAULT_FACEBOOK_URL,
                savedPassword = savedPwd,
                activeUserAgentId = activeUa.id,
                activeUserAgent = activeUa,
                customUserAgentString = customUaString,
                showTelegramPopup = !isTelegramShown
            )
        }
    }

    fun dismissTelegramPopup() {
        prefs.edit().putBoolean("telegram_popup_shown_v1", true).apply()
        _uiState.update { it.copy(showTelegramPopup = false) }
    }

    fun openTelegramChannel(context: Context) {
        dismissTelegramPopup()
        val url = AppSecurity.getTelegramUrl()
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Link: $url"))
            }
        }
    }

    fun selectUserAgent(ua: UserAgentItem) {
        prefs.edit().putString("last_ua_id", ua.id).apply()
        _uiState.update {
            it.copy(
                activeUserAgentId = ua.id,
                activeUserAgent = ua,
                showUaSheet = false
            )
        }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ReloadWebView)
            _uiEvent.emit(UiEvent.ShowSnackbar("User-Agent: ${ua.name}"))
        }
    }

    fun setCustomUserAgent(customUa: String) {
        if (customUa.isBlank()) return
        prefs.edit().putString("custom_ua_string", customUa).apply()
        
        val customUaItem = UserAgentItem(
            id = "ua_custom",
            name = "Custom User-Agent",
            userAgent = customUa,
            description = "User specified User-Agent string",
            iconType = "custom"
        )

        prefs.edit().putString("last_ua_id", "ua_custom").apply()

        _uiState.update {
            it.copy(
                customUserAgentString = customUa,
                activeUserAgentId = "ua_custom",
                activeUserAgent = customUaItem,
                showCustomUaDialog = false,
                showUaSheet = false
            )
        }

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ReloadWebView)
            _uiEvent.emit(UiEvent.ShowSnackbar("Applied Custom User-Agent"))
        }
    }

    fun savePassword(pwd: String) {
        prefs.edit().putString("saved_fb_password", pwd).apply()
        _uiState.update {
            it.copy(
                savedPassword = pwd,
                showPasswordDialog = false
            )
        }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar("Facebook Password Saved!"))
        }
    }

    fun copyPassword(context: Context) {
        val pwd = _uiState.value.savedPassword
        if (pwd.isBlank()) {
            _uiState.update { it.copy(showPasswordDialog = true) }
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("No password saved. Enter password to save."))
            }
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Password", pwd)
            clipboard.setPrimaryClip(clip)
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Password copied to clipboard!"))
            }
        }
    }

    fun copyFacebookUid(context: Context) {
        try {
            val cookieManager = CookieManager.getInstance()
            val cookies = cookieManager.getCookie("https://web.facebook.com")
                ?: cookieManager.getCookie("https://facebook.com")
                ?: cookieManager.getCookie("https://m.facebook.com")
                ?: ""

            val uidRegex = Regex("c_user=([0-9]+)")
            val match = uidRegex.find(cookies)

            if (match != null) {
                val uid = match.groupValues[1]
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Facebook UID", uid)
                clipboard.setPrimaryClip(clip)
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Facebook UID copied: $uid"))
                }
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Facebook UID not found. Please log in first."))
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Failed to get Facebook UID"))
            }
        }
    }

    fun copyCookies(context: Context) {
        try {
            val cookieManager = CookieManager.getInstance()
            val cookies = cookieManager.getCookie("https://web.facebook.com")
                ?: cookieManager.getCookie("https://facebook.com")
                ?: cookieManager.getCookie("https://m.facebook.com")
                ?: ""

            if (cookies.isNotBlank()) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Cookies", cookies)
                clipboard.setPrimaryClip(clip)
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Website Cookies copied to clipboard!"))
                }
            } else {
                viewModelScope.launch {
                    _uiEvent.emit(UiEvent.ShowSnackbar("No cookies found for website."))
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Error copying cookies."))
            }
        }
    }

    fun openFacebookHome() {
        val fbUrl = DEFAULT_FACEBOOK_URL
        _uiState.update { it.copy(currentUrl = fbUrl) }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.LoadUrl(fbUrl))
            _uiEvent.emit(UiEvent.ShowSnackbar("Loaded Default Registration Page"))
        }
    }

    fun updateNavigationState(url: String, canGoBack: Boolean, canGoForward: Boolean, title: String?) {
        _uiState.update {
            it.copy(
                currentUrl = url,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                pageTitle = if (!title.isNullOrBlank()) title else it.pageTitle
            )
        }
    }

    fun updateProgress(progress: Int, isLoading: Boolean) {
        _uiState.update {
            it.copy(
                progress = progress,
                isLoading = isLoading
            )
        }
    }

    fun toggleDesktopMode() {
        val newDesktop = !_uiState.value.isDesktopMode
        _uiState.update { it.copy(isDesktopMode = newDesktop) }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ReloadWebView)
            _uiEvent.emit(UiEvent.ShowSnackbar(if (newDesktop) "Desktop Mode Enabled" else "Mobile Mode Enabled"))
        }
    }

    fun clearCookiesAndCache() {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        openFacebookHome()

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ClearWebViewData)
            _uiEvent.emit(UiEvent.ShowSnackbar("Website Cache & Data Cleared"))
        }
    }

    fun setShowUaSheet(show: Boolean) {
        _uiState.update { it.copy(showUaSheet = show) }
    }

    fun setShowCustomUaDialog(show: Boolean) {
        _uiState.update { it.copy(showCustomUaDialog = show) }
    }

    fun setShowPasswordDialog(show: Boolean) {
        _uiState.update { it.copy(showPasswordDialog = show) }
    }

    fun goBack() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.GoBack)
        }
    }

    fun goForward() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.GoForward)
        }
    }

    fun loadCustomUrl(urlInput: String) {
        var formatted = urlInput.trim()
        if (formatted.isBlank()) return
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "https://$formatted"
        }
        _uiState.update { it.copy(currentUrl = formatted) }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.LoadUrl(formatted))
        }
    }

    private val firstNames = listOf(
        "Rakib", "Tanvir", "Sabbir", "Nusrat", "Anik", "Mehedi", "Fahim", "Mahmud",
        "Sumon", "Tamim", "Jubayer", "Ariful", "Nabil", "Shakil", "Shahadat", "Naim",
        "Abrar", "Rifat", "Tahmid", "Ashik", "Robiul", "Imran", "Farhan", "Kawsar",
        "Alex", "David", "Michael", "James", "Daniel", "Ethan"
    )

    private val lastNames = listOf(
        "Hossain", "Ahmed", "Khan", "Chowdhury", "Islam", "Rahman", "Miah", "Sarkar",
        "Alam", "Uddin", "Hasan", "Ali", "Akter", "Kazi", "Bhowmik", "Das", "Roy",
        "Smith", "Johnson", "Miller", "Taylor", "Wilson"
    )

    fun copyRandomFirstName(context: Context) {
        val randomFirstName = firstNames.random()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("First Name", randomFirstName)
        clipboard.setPrimaryClip(clip)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar("First Name copied: $randomFirstName"))
        }
    }

    fun copyRandomLastName(context: Context) {
        val randomLastName = lastNames.random()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Surname", randomLastName)
        clipboard.setPrimaryClip(clip)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar("Surname copied: $randomLastName"))
        }
    }

    fun triggerTimedAutofill() {
        val savedPassword = _uiState.value.savedPassword.ifBlank {
            "Pass" + (100000..999999).random() + "@!"
        }
        val randomFirstName = firstNames.random()
        val randomLastName = lastNames.random()

        val jsScript = """
            (function() {
                function setNativeValue(element, value) {
                    if (!element) return;
                    const valueSetter = Object.getOwnPropertyDescriptor(element, 'value') || {};
                    const prototype = Object.getPrototypeOf(element);
                    const prototypeValueSetter = Object.getOwnPropertyDescriptor(prototype, 'value') || {};
                    const setter = valueSetter.set || prototypeValueSetter.set;
                    if (setter) {
                        setter.call(element, value);
                    } else {
                        element.value = value;
                    }
                    element.dispatchEvent(new Event('input', { bubbles: true }));
                    element.dispatchEvent(new Event('change', { bubbles: true }));
                    element.dispatchEvent(new Event('blur', { bubbles: true }));
                }

                const fn = "$randomFirstName";
                const ln = "$randomLastName";
                const pwd = "$savedPassword";

                const fnInputs = document.querySelectorAll('input[name*="firstname"], input[name*="first_name"], input[autocomplete="given-name"], input[id*="firstname"]');
                const lnInputs = document.querySelectorAll('input[name*="lastname"], input[name*="last_name"], input[autocomplete="family-name"], input[id*="lastname"]');
                const passInputs = document.querySelectorAll('input[type="password"], input[name*="pass"]');

                let delay = 300;

                // Step 1: First Name
                if (fnInputs.length > 0) {
                    setTimeout(function() {
                        fnInputs[0].focus();
                        setNativeValue(fnInputs[0], fn);
                    }, delay);
                    delay += 1400;
                }

                // Step 2: Last Name
                if (lnInputs.length > 0) {
                    setTimeout(function() {
                        lnInputs[0].focus();
                        setNativeValue(lnInputs[0], ln);
                    }, delay);
                    delay += 1400;
                }

                // Step 3: Password
                if (passInputs.length > 0) {
                    setTimeout(function() {
                        passInputs[0].focus();
                        setNativeValue(passInputs[0], pwd);
                    }, delay);
                    delay += 1400;
                }

                // Step 4: Birthday Day/Month/Year
                const daySel = document.querySelector('select[name="birthday_day"], select[id="day"]');
                if (daySel) {
                    setTimeout(function() {
                        daySel.value = String(Math.floor(Math.random() * 25) + 1);
                        daySel.dispatchEvent(new Event('change', { bubbles: true }));
                    }, delay);
                    delay += 1000;
                }

                const monthSel = document.querySelector('select[name="birthday_month"], select[id="month"]');
                if (monthSel) {
                    setTimeout(function() {
                        monthSel.value = String(Math.floor(Math.random() * 12) + 1);
                        monthSel.dispatchEvent(new Event('change', { bubbles: true }));
                    }, delay);
                    delay += 1000;
                }

                const yearSel = document.querySelector('select[name="birthday_year"], select[id="year"]');
                if (yearSel) {
                    setTimeout(function() {
                        yearSel.value = String(Math.floor(1995 + Math.random() * 10));
                        yearSel.dispatchEvent(new Event('change', { bubbles: true }));
                    }, delay);
                }
            })();
        """.trimIndent()

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.EvaluateJavascript(jsScript))
            _uiEvent.emit(UiEvent.ShowSnackbar("Step-by-step AutoFill started..."))
        }
    }
}

