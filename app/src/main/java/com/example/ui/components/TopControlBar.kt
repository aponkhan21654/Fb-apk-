package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.WebViewState

@Composable
fun TopControlBar(
    state: WebViewState,
    onOpenFacebook: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onCopyUid: () -> Unit,
    onCopyPassword: () -> Unit,
    onOpenPasswordDialog: () -> Unit,
    onCopyCookies: () -> Unit,
    onClearWebsites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val facebookBlue = Color(0xFF1877F2)
    val buttonBg = Color(0xFFE8F0FC)
    val barBg = Color(0xFFF7F9FC)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(barBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Desktop Mode ON/OFF
            TopNavButton(
                isSelected = state.isDesktopMode,
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onToggleDesktopMode,
                testTag = "top_button_desktop"
            ) {
                Icon(
                    imageVector = Icons.Default.CropSquare,
                    contentDescription = "Desktop Mode Toggle",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 2. Open https://web.facebook.com/ link
            TopNavButton(
                isSelected = state.currentUrl.contains("facebook.com"),
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onOpenFacebook,
                testTag = "top_button_open_fb"
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Open Facebook",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 3. Facebook ID / UID Copy
            TopNavButton(
                isSelected = false,
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onCopyUid,
                testTag = "top_button_copy_uid"
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Copy UID",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 4. Password Copy (Click) & Set Password (Hold/Long-Click)
            TopNavButton(
                isSelected = state.savedPassword.isNotBlank(),
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onCopyPassword,
                onLongClick = onOpenPasswordDialog,
                testTag = "top_button_password"
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Copy/Set Password",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 5. Website Cookie Copy
            TopNavButton(
                isSelected = false,
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onCopyCookies,
                testTag = "top_button_copy_cookies"
            ) {
                Icon(
                    imageVector = Icons.Default.Cookie,
                    contentDescription = "Copy Cookies",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 6. Clear Website Data & Link
            TopNavButton(
                isSelected = false,
                activeColor = facebookBlue,
                bgColor = buttonBg,
                onClick = onClearWebsites,
                testTag = "top_button_clear_data"
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Website Data",
                    tint = facebookBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Loading Progress Indicator
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { state.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = facebookBlue,
                trackColor = barBg
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopNavButton(
    isSelected: Boolean,
    activeColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    testTag: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(46.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        tonalElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            content()

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.65f)
                        .height(3.dp)
                        .background(
                            activeColor,
                            shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                        )
                )
            }
        }
    }
}


