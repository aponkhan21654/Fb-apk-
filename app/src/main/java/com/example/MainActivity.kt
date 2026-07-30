package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.TokenViewModel
import com.example.ui.UiEvent
import com.example.ui.components.BottomNavBar
import com.example.ui.components.CustomUaDialog
import com.example.ui.components.SetPasswordDialog
import com.example.ui.components.TelegramJoinDialog
import com.example.ui.components.TopControlBar
import com.example.ui.components.UserAgentBottomSheet
import com.example.ui.components.WebViewContainer
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TokenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TokenSwitcherApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TokenSwitcherApp(viewModel: TokenViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Listen to ViewModel UI events (Toast / Snackbar)
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            if (event is UiEvent.ShowSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopControlBar(
                state = state,
                onOpenFacebook = { viewModel.openFacebookHome() },
                onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                onCopyUid = { viewModel.copyFacebookUid(context) },
                onCopyPassword = { viewModel.copyPassword(context) },
                onOpenPasswordDialog = { viewModel.setShowPasswordDialog(true) },
                onCopyCookies = { viewModel.copyCookies(context) },
                onClearWebsites = { viewModel.clearCookiesAndCache() }
            )
        },
        bottomBar = {
            BottomNavBar(
                state = state,
                onBackClick = { viewModel.goBack() },
                onForwardClick = { viewModel.goForward() },
                onReloadClick = { viewModel.loadCustomUrl(state.currentUrl) },
                onHomeClick = { viewModel.openFacebookHome() },
                onOpenUaSheet = { viewModel.setShowUaSheet(true) },
                onClearCookiesClick = { viewModel.clearCookiesAndCache() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            WebViewContainer(
                state = state,
                uiEvents = viewModel.uiEvent,
                onNavigationStateChanged = { url, canGoBack, canGoForward, title ->
                    viewModel.updateNavigationState(url, canGoBack, canGoForward, title)
                },
                onProgressChanged = { progress, isLoading ->
                    viewModel.updateProgress(progress, isLoading)
                }
            )

            // Floating Action Buttons Row (Auto Fill + Copy Name)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auto Fill (Timed)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF107C41),
                    contentColor = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = { viewModel.triggerTimedAutofill() }
                        )
                        .testTag("fab_autofill_timed")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto Fill Form",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Auto Fill",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }

                // Copy Name (Click: First Name, Hold: Surname)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1877F2),
                    contentColor = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .combinedClickable(
                            onClick = { viewModel.copyRandomFirstName(context) },
                            onLongClick = { viewModel.copyRandomLastName(context) }
                        )
                        .testTag("fab_copy_random_name")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Copy Random Name",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Copy Name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // User Agent Bottom Sheet
        if (state.showUaSheet) {
            UserAgentBottomSheet(
                userAgentList = state.userAgentList,
                activeUserAgentId = state.activeUserAgentId,
                customUserAgentString = state.customUserAgentString,
                onSelectUserAgent = { ua -> viewModel.selectUserAgent(ua) },
                onOpenCustomUaDialog = {
                    viewModel.setShowUaSheet(false)
                    viewModel.setShowCustomUaDialog(true)
                },
                onDismiss = { viewModel.setShowUaSheet(false) }
            )
        }

        // Set Password Dialog
        if (state.showPasswordDialog) {
            SetPasswordDialog(
                currentPassword = state.savedPassword,
                onSavePassword = { pwd -> viewModel.savePassword(pwd) },
                onDismiss = { viewModel.setShowPasswordDialog(false) }
            )
        }

        // Custom UA Dialog
        if (state.showCustomUaDialog) {
            CustomUaDialog(
                currentCustomUa = state.customUserAgentString,
                onSaveCustomUa = { customUa ->
                    viewModel.setCustomUserAgent(customUa)
                },
                onDismiss = { viewModel.setShowCustomUaDialog(false) }
            )
        }

        // Telegram Join Popup on First Launch
        if (state.showTelegramPopup) {
            TelegramJoinDialog(
                onJoin = { viewModel.openTelegramChannel(context) },
                onDismiss = { viewModel.dismissTelegramPopup() }
            )
        }
    }
}

