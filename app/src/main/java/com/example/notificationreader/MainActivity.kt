package com.example.notificationreader

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var receiver: NotificationReceiver

    @Composable
    fun NotificationUI(
        notifications: List<String>,
        onGrantAccess: () -> Unit,
        onClear: () -> Unit
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text("Received Notifications:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(notifications) { msg ->
                    Text(text = msg, style = MaterialTheme.typography.bodyMedium)
                    Divider()
                }
            }

            Spacer(Modifier.height(16.dp))
            Row {
                Button(onClick = onGrantAccess, modifier = Modifier.weight(1f)) {
                    Text("Grant Access")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onClear, modifier = Modifier.weight(1f)) {
                    Text("Clear")
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationListState = mutableStateListOf<String>()

        receiver = NotificationReceiver { message ->
            notificationListState.add(message)
        }

        val intentFilter = IntentFilter("com.example.notificationreader.NOTIFICATION_LISTENER")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, intentFilter)
        }

        setContent {
            MaterialTheme {
                NotificationUI(
                    notifications = notificationListState,
                    onGrantAccess = {
                        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onClear = {
                        notificationListState.clear()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
