package com.example.practicaadicionalpython

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.practicaadicionalpython.ui.screens.ImageEditorScreen
import com.example.practicaadicionalpython.ui.theme.PracticaAdicionalPythonTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // "context" must be an Activity, Service or Application object from your app.
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
            Log.i(TAG, "Python started successfully")
        }

        setContent {
            PracticaAdicionalPythonTheme {
                ImageEditorScreen()
            }
        }
    }
}