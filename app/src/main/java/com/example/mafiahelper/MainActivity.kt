package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
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
                    // IconSelectionDropdown(icons = getAllIconsFromDb(context = this@MainActivity))
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



@SuppressLint("ResourceAsColor")
@Composable
fun LoadingScreen(navController: NavHostController) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(ContextCompat.getColor(context, R.color.main_blue_white))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Maff Helper",
                style = TextStyle(fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CircularProgressIndicator(color = Color(ContextCompat.getColor(context, R.color.main_red)))
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (isDatabaseReady(context)) {
            navController.navigate("otherScreen")
        }
    }
}



/**
 * 401 - глаза - мирный?
 * 439 - лицо мужчины - мирный?
 * 459 - маска демона - мафия?
 * 461 - с нимбом - бесмертный?
 * 465 - череп - мертвый?
 * 460 - призрак - Мертвый?
 * 502 - взрыв - камикадзе?
 * 757 - черное сердце - путана?
 * 818 -  меч - мафия действие?
 * 866 - скорая помощь - доктор действие?
 * 889 - мигалка сирены - шериф действие?
 * 1126, 1131, 1262 - значек медицины - доктор действие
 *
 * 1246 - значек записи - проверка шерифа?
 * 231 - медаль нагрудная - шериф?
 */
@Composable
fun IconSelectionDropdown(icons: List<Icon>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf(icons.firstOrNull()) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "\t${ selectedIcon?.id.toString() } ${selectedIcon?.code}" ?: "Выберите иконку",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true }),
            style = TextStyle(fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif)
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (expanded) {
            Popup(onDismissRequest = { expanded = false }) {
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .offset(0.dp, 50.dp)) {
                    items(icons) { icon ->
                        Text(
                            text = "\t ${icon.id} - ${icon.code}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIcon = icon
                                    expanded = false
                                },
                            style = TextStyle(fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif)
                        )
                    }
                }
            }
        }
    }
}

suspend fun isDatabaseReady(context: Context): Boolean {
    val db = DbHelper(context = context, factory = null)
    return withContext(context = Dispatchers.IO) { db.checkAndResetDataBase() }
}

fun getAllIconsFromDb(context: Context): List<Icon> {
    val db = DbHelper(context = context, factory = null)
    return db.getAllIcons()
}