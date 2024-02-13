package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TimerState {
    Stopped, Running, Paused
}

class MainActivity : ComponentActivity() {
    private var backPressedOnce = false
    private var lastBackPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            dispatcher?.addCallback(this) {
                if (navController.currentDestination?.route == "gameScreen") {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressedTime < 2000) {
                        finish() // Закрыть приложение при двойном нажатии в течение 2 секунд
                    } else {
                        lastBackPressedTime = currentTime
                        Toast.makeText(applicationContext, "Нажмите еще раз, чтобы закрыть приложение", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    this.remove() // Удалите этот колбек
                    navController.popBackStack() // Вызовите стандартное поведение кнопки "Назад"
                }
            }

            NavHost(navController = navController, startDestination = "loadingScreen") {
                composable("loadingScreen") {
                    LoadingScreen(navController)
                }
                composable("otherScreen") {
                    // IconSelectionDropdown(icons = getAllIconsFromDb(context = this@MainActivity))
                    LetsStartScreen(navController)
                }
                composable("gameScreen") {
                    GameScreen(navController)
                }
            }
        }
    }

}
@SuppressLint("ResourceAsColor")
@Composable
fun LetsStartScreen(navController: NavHostController) {
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
            Button(onClick = { navController.navigate("gameScreen") }) {
                Text(text = "Начать игру")
            }
        }
    }
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

@Composable
fun PreGameScreen() {
    var players = mutableListOf<Player>()
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Maff Helper")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .height(20.dp)
                    .weight(1f)
                    .border(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "+ игрок")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier
                    .height(20.dp)
                    .weight(1f)
                    .border(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Раздать роли")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier
                    .height(20.dp)
                    .weight(1f)
                    .border(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "начать игру")
            }
        }
    }
}
@Composable
fun GameScreen(navController: NavHostController) {
    // ваш код здесь
}

@Composable
fun TimerComponent() {
    var timeLeft by remember { mutableStateOf(600_000L) } // 10 minutes in milliseconds
    var timerState by remember { mutableStateOf(TimerState.Stopped) }
    val timer = rememberCoroutineScope()

    Column {
        Text(
            text = "${timeLeft / 60_000}:${(timeLeft % 60_000) / 1000}",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { if (timeLeft < 600_000) timeLeft += 10_000 }) { Text("+10сек") }
            Button(onClick = { if (timeLeft < 540_000) timeLeft += 60_000 }) { Text("+1мин") }
            Button(onClick = { if (timeLeft > 10_000) timeLeft -= 10_000 }) { Text("-10сек") }
            Button(onClick = { if (timeLeft > 60_000) timeLeft -= 60_000 }) { Text("-1мин") }
        }

        Row {
            Button(onClick = {
                if (timerState == TimerState.Running) {
                    timerState = TimerState.Paused
                } else {
                    timerState = TimerState.Running
                    timer.launch {
                        while (timeLeft > 0 && timerState == TimerState.Running) {
                            delay(1000)
                            timeLeft -= 1000
                        }
                    }
                }
            }) {
                Text(if (timerState == TimerState.Running) "Остановить" else "Запустить")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                timerState = TimerState.Stopped
                timeLeft = 600_000
            }) { Text("Сбросить") }
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
            text = "\t${ selectedIcon?.id.toString() } ${selectedIcon?.code}",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true }),
            style = TextStyle(fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif)
        )
        if (expanded) {
            Popup(onDismissRequest = { expanded = false }) {
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