package com.ovais.ndjsonparser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ovais.ndjsonparser.di.AppModule
import com.ovais.ndjsonparser.presentation.ui.screen.NDJsonParserScreen
import com.ovais.ndjsonparser.ui.theme.NDJsonParserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = AppModule.provideNDJsonViewModel(this)

        setContent {
            NDJsonParserTheme {
                NDJsonParserScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}