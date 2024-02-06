package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "loadingScreen") {
                composable("loadingScreen") {
                    LoadingScreen(navController)
                }
                composable("otherScreen") {
                    OtherScreen()
                }
            }
        }
    }

}
@Composable
fun OtherScreen() {
    Text(text = "Maff Helper")
}

@Composable
fun LoadingScreen(navController: NavHostController) {
    Text(text = "Maff Helper")
    CircularProgressIndicator()

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val db = DbHelper(context = context, factory = null)
        val isDatabaseReady = withContext(context = Dispatchers.IO) { db.checkAndResetDataBase() }

        if (isDatabaseReady) {
            println(isDatabaseReady)
            // Если база данных готова, перенаправляем пользователя на другой экран
            navController.navigate("otherScreen")
        }
    }
}