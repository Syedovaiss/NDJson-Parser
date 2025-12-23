package com.ovais.ndjsonparser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ovais.ndjsonparser.presentation.ui.screen.NDJsonParserScreen
import com.ovais.ndjsonparser.presentation.viewmodel.NDJsonViewModel
import com.ovais.ndjsonparser.ui.theme.NDJsonParserTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class NDJsonParserActivity : ComponentActivity() {
    private val viewModel: NDJsonViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NDJsonParserTheme {
                NDJsonParserScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}