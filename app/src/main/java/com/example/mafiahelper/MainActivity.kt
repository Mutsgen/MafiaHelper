package com.example.mafiahelper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
import kotlin.math.floor


enum class TimerState {
    Stopped, Running, Paused
}

class MainActivity : ComponentActivity() {
    var game: MutableState<Game?> = mutableStateOf(null)
    private var backPressedOnce = false
    private var lastBackPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            dispatcher!!.addCallback(this) {
                if (navController.currentDestination?.route == "preGameScreen") {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressedTime < 2000) {
                        finish() // Закрыть приложение при двойном нажатии в течение 2 секунд
                    } else {
                        lastBackPressedTime = currentTime
                        Toast.makeText(
                            applicationContext,
                            "Нажмите еще раз, чтобы закрыть приложение",
                            Toast.LENGTH_SHORT
                        ).show()
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
                composable("letsStartScreen") {
                    // IconSelectionDropdown(icons = getAllIconsFromDb(context = this@MainActivity))
                    LetsStartScreen(navController)
                }
                composable("preGameScreen") {
                    PreGameScreen(navController, game)
                }
                composable("gameScreen") {
                    GameScreen(navController, game)
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
                text = "Maff Helper", style = TextStyle(
                    fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("preGameScreen") }) {
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
                text = "Maff Helper", style = TextStyle(
                    fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            CircularProgressIndicator(
                color = Color(
                    ContextCompat.getColor(
                        context, R.color.main_red
                    )
                )
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (isDatabaseReady(context)) {
            navController.navigate("letsStartScreen")
        }
    }
}

@Composable
fun PreGameScreen(navController: NavHostController, game: MutableState<Game?>) {
    val context = LocalContext.current


    /**
     * 0 - мирный
     * 1 - мафия
     * 2 - шериф
     * 3 - доктор
     */
    var roles = getBaseRoles(context)
    var players = remember { mutableStateOf<List<Player>>(listOf()) }
    if (roles != null && players.value.isEmpty()) {
        val newPlayers = mutableListOf<Player>()
        for (role in roles) {
            newPlayers.add(Player(newPlayers.size.toUInt() + 1u, "", roles[0]))
        }
        players.value = newPlayers
    }
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(ContextCompat.getColor(context, R.color.main_blue_white)))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    color = Color(
                        ContextCompat.getColor(
                            context, R.color.secondary_red
                        )
                    )
                ), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Maff Helper", style = TextStyle(
                    fontSize = 34.sp, color = Color.Black, fontFamily = FontFamily.SansSerif
                ), modifier = Modifier.offset(0.dp, 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(modifier = Modifier
                .height(60.dp)
                .weight(1f)
                .border(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = {
                    if (players.value.size < 12) players.value =
                        players.value + Player(players.value[players.value.size - 1]._number + 1u, "", roles!![0])
                }) {
                Text(text = "+ игрок", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(modifier = Modifier
                .height(60.dp)
                .weight(1f)
                .border(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = {
                    val whiteRoles =
                        roles!!.filter { it.team == 0.toShort() && it.id != 1u }.toMutableList()
                    val redRoles = roles.filter { it.team == 1.toShort() }

                    val updatedPlayers = players.value.toMutableList()
                    updatedPlayers.forEach { player -> player.updatePlayer(player._name, roles[0]) }
                    var whiteCount = whiteRoles.size
                    var redCount = floor(updatedPlayers.size / 4f).toInt()

                    println(whiteCount)
                    println(redCount)

                    while (players.value.size >= 4 && (whiteCount > 0 || redCount > 0)) {
                        val playerIndex =
                            updatedPlayers.indices.filter { updatedPlayers[it]._role.id == 1u }
                                .random()
                        if (whiteRoles.size > 0) {
                            --whiteCount
                            updatedPlayers[playerIndex].updatePlayer(
                                updatedPlayers[playerIndex]._name,
                                whiteRoles.removeAt(0)
                            )
                            continue
                        } else if (redRoles.isNotEmpty()) {
                            --redCount
                            updatedPlayers[playerIndex].updatePlayer(
                                updatedPlayers[playerIndex]._name,
                                redRoles.random()
                            )
                            continue
                        }
                    }
                    // жесткий костыль, дабы переотрисовывать нормально
                    updatedPlayers.add(Player(players.value.size.toUInt() + 1u, "", roles[0]))
                    players.value = updatedPlayers
                    players.value = (players.value as MutableList<Player>).dropLast(1)
                    players.apply { }

                }) {
                Text(text = "Раздать роли", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(modifier = Modifier
                .height(60.dp)
                .weight(1f)
                .border(3.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                onClick = {
                    game.value = Game(players.value)
                    navController.navigate("gameScreen")
                }) {
                Text(text = "Начать игру", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(ContextCompat.getColor(context, R.color.main_blue_white)))
        ) {
            if (roles != null) {
                PlayerTable(players, roles)
            }
        }
    }

}

@Composable
fun PlayerTable(players: MutableState<List<Player>>, roles: List<Role>) {
    val context = LocalContext.current
    LazyColumn(
        Modifier
            .background(
                color = Color(
                    ContextCompat.getColor(
                        context, R.color.main_blue_white
                    )
                )
            )
            .border(1.dp, Color.Black)
    ) {
        items(players.value.size) { index ->
            PlayerRow(players.value[index], index, roles, players)
        }
    }
}

@Composable
fun PlayerRow(player: Player, index: Int, roles: List<Role>, players: MutableState<List<Player>>) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(player._name) }
    var role by remember { mutableStateOf(player._role) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(50.dp)
                .align(Alignment.CenterVertically)
        ) {
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(35.dp)
                    .padding(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(ContextCompat.getColor(context, R.color.glassyGray)),
                    contentColor = Color.White // Adjust as needed
                ),
                shape = RoundedCornerShape(size = 5.dp), // Or another shape as per your design requirements
                border = BorderStroke(0.dp, Color.Transparent), // This removes the border
                onClick = { val playerList = players.value.toMutableList()
                    playerList.remove(player)
                    players.value = playerList }
            ) {
                Text(
                    text = "X",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center) // This centers the text within the button
                )
            }
        }
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(50.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = player._number.toString(), fontSize = 30.sp, modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
                .padding(0.dp)
        ) {
            OutlinedTextField(textStyle = TextStyle(fontSize = 18.sp),
                value = name!!,
                onValueChange = { newName ->
                    name = newName
                    updatePlayer(index, players, newName, role)
                },
                singleLine = true,
                placeholder = { Text("Введите имя") })
        }
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            RoleSelector(player, roles, players)
        }
    }
}

@Composable
fun RoleSelector(player: Player, roles: List<Role>, players: MutableState<List<Player>>) {
    var selectedRole by remember { mutableStateOf(player._role) }

    LaunchedEffect(player._role) {
        selectedRole = player._role
    }

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        var expanded by remember { mutableStateOf(false) }

        Text(
            selectedRole.name,
            Modifier
                .clickable { expanded = true }
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(.7f),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            roles.forEach { role ->
                DropdownMenuItem(onClick = {
                    selectedRole = role
                    player._role = role
                    expanded = false
                }) {
                    Text(role.name)
                }
            }
        }
    }
}

@Composable
fun GameScreen(navController: NavHostController, game:  MutableState<Game?>) {
    Text(text = game.value.toString())
}

@Composable
fun TimerComponent() {
    val context = LocalContext.current
    var timeLeft by remember { mutableLongStateOf(600_000L) } // 10 minutes in milliseconds
    var timerState by remember { mutableStateOf(TimerState.Stopped) }
    val timer = rememberCoroutineScope()

    Column {
        Modifier
            .height(400.dp)
            .fillMaxWidth()

        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .border(2.dp, Color(ContextCompat.getColor(context, R.color.main_blue_white)))
        ) {
            Text(
                text = "${String.format("%02d", timeLeft / 60_000)}:${
                    String.format(
                        "%02d", (timeLeft % 60_000) / 1000
                    )
                }", fontSize = 100.sp, modifier = Modifier.align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { if (timeLeft < 600_000) timeLeft += 10_000 }) { Text("+10сек") }
            Button(onClick = { if (timeLeft < 540_000) timeLeft += 60_000 }) { Text("+1мин") }
            Button(onClick = { if (timeLeft > 10_000) timeLeft -= 10_000 }) { Text("-10сек") }
            Button(onClick = { if (timeLeft > 60_000) timeLeft -= 60_000 }) { Text("-1мин") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                if (timerState == TimerState.Running) {
                    timerState = TimerState.Paused
                } else {
                    timerState = TimerState.Running
                    timer.launch {
                        while (timeLeft > 0 && timerState == TimerState.Running) {
                            delay(1000)
                            if (timerState == TimerState.Running) timeLeft -= 1000
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
            text = "\t${selectedIcon?.id.toString()} ${selectedIcon?.code}",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true }),
            style = TextStyle(
                fontSize = 42.sp, color = Color.Black, fontFamily = FontFamily.SansSerif
            )
        )
        if (expanded) {
            Popup(onDismissRequest = { expanded = false }) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .offset(0.dp, 50.dp)
                ) {
                    items(icons) { icon ->
                        Text(
                            text = "\t ${icon.id} - ${icon.code}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIcon = icon
                                    expanded = false
                                },
                            style = TextStyle(
                                fontSize = 42.sp,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif
                            )
                        )
                    }
                }
            }
        }
    }
}

fun updatePlayer(index: Int, players: MutableState<List<Player>>, name: String, role: Role) {
    players.value = players.value.toMutableList().also {
        it[index] = it[index].updatePlayer(name = name, role = role)
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

fun getBaseRoles(context: Context): List<Role>? {
    val db = DbHelper(context = context, factory = null)
    return db.getBaseRoles(context)
}