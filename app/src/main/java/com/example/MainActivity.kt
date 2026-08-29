package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.screens.TalkLoopMainScreen
import com.example.ui.theme.TalkLoopTheme
import com.example.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

  private val authViewModel: AuthViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TalkLoopTheme {
        TalkLoopMainScreen(
          viewModel = authViewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

