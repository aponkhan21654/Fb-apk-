package com.example.data

import com.example.model.TokenItem
import com.example.model.UserAgentItem

object Presets {

    val DEFAULT_TOKENS = listOf(
        TokenItem(
            id = "token_1",
            label = "Token #1 (Full FB Reg)",
            url = "https://m.facebook.com/mreg?e_token=AblG8NgRXtweT10VmrGryeTusY7yPTcp-YEfqQDK7R3YlKmypw9Ox4wsxK_-s82mFlfcrEwGwTOKNQ&d_hash=80AE5E5572F616E99079B0A2D3596C24&cid=256002347743983&app_version=310&tg=201&cct=1&src=1&soft=hjk"
        ),
        TokenItem(
            id = "token_2",
            label = "Token #2 (AbnQFQ...)",
            url = "https://m.facebook.com/mreg?e_token=AbnQFQG4x_sBJ1BS1HgYin1ijehpcfvN7TMPWiX9EUc3ccMDKbce7V9FPzk7AbMoPsA7K5nonavUvw&d_hash=FBA"
        ),
        TokenItem(
            id = "token_3",
            label = "Token #3 (AblG8N...)",
            url = "https://m.facebook.com/mreg?e_token=AblG8NgRXtweT10VmrGryeTusY7yPTcp-YEfqQDK7R3YlKmypw9Ox4wsxK_-s82mFlfcrEwGwTOKNQ&d_hash=80AE5"
        ),
        TokenItem(
            id = "token_4",
            label = "Token #4 (Abm6tg...)",
            url = "https://m.facebook.com/mreg?e_token=Abm6tgf10M_vK4TV2uawjG-ae8fFrddyzOf_FcUdJbRfjbkIcTrlIUJHoz7w6Vz4so8TOGWIcDgx0Q&d_hash=FBA71FDC8239E901"
        ),
        TokenItem(
            id = "token_5",
            label = "Token #5 (Abky_3...)",
            url = "https://m.facebook.com/mreg?e_token=Abky_3xr70NBQr4YJM2br-fChpjgWA3dJqiJ6Lm8JJP0XyXEiCg_RIWjY1OtPlcLRduFEgVzOSaDFA&d_hash=FBA71FDC8239E901"
        ),
        TokenItem(
            id = "token_6",
            label = "Token #6 (Abkdgk...)",
            url = "https://m.facebook.com/mreg?e_token=AbkdgkgPLqQ_xmavX1koYXq51xZkP-95Wq96iKw67-6q_CMRimxVmyI8Pa-8jYyE-h7bd9GTcnnylw&d_hash=FBA71FDC8239E901"
        ),
        TokenItem(
            id = "token_7",
            label = "Token #7 (AbliUr...)",
            url = "https://m.facebook.com/mreg?e_token=AbliUrkbMtSgUBgB0Lh6uh-W5ZR_QiE1rB6pQ8mWYPiNQNIoVH7cnQaPiPq6ufSFa5IxfSeOoqHErg&d_hash=FBA71FDC8239E901"
        ),
        TokenItem(
            id = "token_8",
            label = "Token #8 (Abkdgk Full Hash)",
            url = "https://m.facebook.com/mreg?e_token=AbkdgkgPLqQ_xmavX1koYXq51xZkP-95Wq96iKw67-6q_CMRimxVmyI8Pa-8jYyE-h7bd9GTcnnylw&d_hash=FBA71FDC8239E90131BC4314E9B4E92E&cid=256002347743983&app_versio"
        ),
        TokenItem(
            id = "token_9",
            label = "Token #9 (AblG8N App v3)",
            url = "https://m.facebook.com/mreg?e_token=AblG8NgRXtweT10VmrGryeTusY7yPTcp-YEfqQDK7R3YlKmypw9Ox4wsxK_-s82mFlfcrEwGwTOKNQ&d_hash=80AE5E5572F616E99079B0A2D3596C24&cid=256002347743983&app_version=3"
        ),
        TokenItem(
            id = "token_10",
            label = "Token #10 (Abky_3 Ext Hash)",
            url = "https://m.facebook.com/mreg?e_token=Abky_3xr70NBQr4YJM2br-fChpjgWA3dJqiJ6Lm8JJP0XyXEiCg_RIWjY1OtPlcLRduFEgVzOSaDFA&d_hash=FBA71FDC8239E90131BC"
        ),
        TokenItem(
            id = "token_11",
            label = "Token #11 (Base m.facebook)",
            url = "https://m.facebook.com/mreg"
        )
    )

    val DEFAULT_USER_AGENTS = listOf(
        UserAgentItem(
            id = "ua_chrome_android",
            name = "Mobile Chrome (Android)",
            userAgent = "Mozilla/5.0 (Linux; Android 14; SM-S928B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36",
            description = "Standard Android Mobile Chrome Browser",
            iconType = "mobile"
        ),
        UserAgentItem(
            id = "ua_fb_android",
            name = "Facebook In-App Browser (Android)",
            userAgent = "Mozilla/5.0 (Linux; Android 13; SM-G998B Build/TP1A.220624.014; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/115.0.5790.166 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/425.0.0.22.49;]",
            description = "Native Android Facebook In-App Browser (FB4A)",
            iconType = "fb"
        ),
        UserAgentItem(
            id = "ua_iphone_safari",
            name = "iPhone Safari (iOS)",
            userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.3.1 Mobile/15E148 Safari/604.1",
            description = "Mobile Safari on iPhone iOS 17",
            iconType = "ios"
        ),
        UserAgentItem(
            id = "ua_firefox_android",
            name = "Firefox Mobile (Android)",
            userAgent = "Mozilla/5.0 (Android 14; Mobile; rv:124.0) Gecko/124.0 Firefox/124.0",
            description = "Mozilla Firefox Mobile Browser for Android",
            iconType = "firefox"
        ),
        UserAgentItem(
            id = "ua_desktop_chrome",
            name = "Desktop Chrome (Windows)",
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
            description = "Full Desktop View Chrome on Windows 11",
            iconType = "desktop"
        )
    )
}
