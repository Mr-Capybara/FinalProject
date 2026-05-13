package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.ui.ClarityApp
import com.example.finalproject.ui.theme.ClarityPrimary
import com.example.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ClarityViewModel = viewModel()
            val primaryHex by viewModel.primaryColorHex.collectAsState()
            FinalProjectTheme(primaryColor = primaryHex.toComposeColor()) {
                ClarityApp(viewModel = viewModel)
            }
        }
    }
}

private fun String.toComposeColor(): Color {
    return runCatching { Color(android.graphics.Color.parseColor(this)) }.getOrDefault(ClarityPrimary)
}
