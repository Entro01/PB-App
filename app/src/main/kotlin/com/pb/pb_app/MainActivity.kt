package com.pb.pb_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.pb.pb_app.ui.HomeScreen

import com.pb.pb_app.ui.theme.PBAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PBAppTheme {
                HomeScreen()
            }
        }
    }
}