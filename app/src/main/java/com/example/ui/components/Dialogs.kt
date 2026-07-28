package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TelegramJoinDialog(
    onJoin: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Telegram",
                    tint = Color(0xFF229ED9),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Join Telegram", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Welcome to TW FB CREATE!\n\nJoin our official Telegram channel TeamWithApon for updates, support, and tools.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_join_telegram_button")
            ) {
                Text("Join Telegram", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_dismiss_telegram_button")
            ) {
                Text("Later")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SetPasswordDialog(
    currentPassword: String,
    onSavePassword: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var passwordInput by remember { mutableStateOf(currentPassword) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Save Facebook Password", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter your password to save it. Clicking the Key/Lock icon will instantly copy it to clipboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Facebook Password") },
                    placeholder = { Text("Enter password...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSavePassword(passwordInput)
                },
                modifier = Modifier.testTag("dialog_save_password_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomUaDialog(
    currentCustomUa: String,
    onSaveCustomUa: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var uaInput by remember { mutableStateOf(currentCustomUa) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Set Custom User-Agent", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Specify any custom browser User-Agent string to emulate.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uaInput,
                    onValueChange = { uaInput = it },
                    label = { Text("User-Agent String") },
                    placeholder = { Text("Mozilla/5.0 ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_custom_ua_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (uaInput.isNotBlank()) {
                        onSaveCustomUa(uaInput)
                    }
                },
                modifier = Modifier.testTag("dialog_save_ua_button")
            ) {
                Text("Apply User-Agent")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

