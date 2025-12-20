package ih.pam.pamobile_jelahjahmalang.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ih.pam.pamobile_jelahjahmalang.R
import ih.pam.pamobile_jelahjahmalang.viewmodel.MissionViewmodel
import ih.pam.pamobile_jelahjahmalang.viewmodel.UserViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val userViewModel: UserViewmodel = viewModel()
    val missionViewModel: MissionViewmodel = viewModel()

    val user by userViewModel.user.collectAsState()
    val missions by missionViewModel.missions.collectAsState()

    val completedMissionId =
        user?.fields?.completedMissions?.arrayValue?.values?.map { it.value } ?: emptyList()

    val activeMission = missions.size - completedMissionId.size


    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        userViewModel.fetchUser()
        missionViewModel.fetchMissions()
    }

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight

        Scaffold(
            containerColor = Color(0xffF4F8FF),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Mission Explore",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xffF4F8FF)
                    )
                )
            }
        ) { padding ->

            if (!isLandscape) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    state = listState
                ) {

                    item {
                        StatusCard(user?.fields?.totalPoints?.value ?: "0", doneMission = completedMissionId.size.toString(), activeMission = activeMission.toString() )
                        Spacer(Modifier.height(32.dp))
                        Text("Daftar Misi", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(16.dp))
                    }

                    items(missions.size) { index ->
                        val item = missions[index]
                        val fields = item.fields
                        val isCompleted =
                            completedMissionId.contains(item.getCleanId())

                        MissionCard(
                            modifier = Modifier,
                            onExploreClick = {
                                navController.navigate("detail_mission/${item.getCleanId()}")
                            },
                            judul = fields?.title?.value ?: "-",
                            level = fields?.level?.value ?: "easy",
                            alamat1 = fields?.locationName?.value ?: "-",
                            alamat2 = fields?.address?.value ?: "-",
                            misi = fields?.missionTask?.value ?: "-",
                            poin = fields?.points?.value?.toIntOrNull() ?: 0,
                            isCompleted = isCompleted
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            } else {
                LazyRow(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    state = listState,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(missions.size) { index ->
                        val item = missions[index]
                        val fields = item.fields
                        val isCompleted =
                            completedMissionId.contains(item.getCleanId())

                        MissionCard(
                            modifier = Modifier.width(340.dp),
                            onExploreClick = {
                                navController.navigate("detail_mission/${item.getCleanId()}")
                            },
                            judul = fields?.title?.value ?: "-",
                            level = fields?.level?.value ?: "easy",
                            alamat1 = fields?.locationName?.value ?: "-",
                            alamat2 = fields?.address?.value ?: "-",
                            misi = fields?.missionTask?.value ?: "-",
                            poin = fields?.points?.value?.toIntOrNull() ?: 0,
                            isCompleted = isCompleted
                        )
                        Spacer(Modifier.width(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(totalPoin: String, activeMission: String, doneMission: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusItem("Poin", totalPoin, R.drawable.oin, Color(0xffF0B100))
            VerticalDivider()
            StatusItem("Aktif", activeMission, R.drawable.available, Color(0xff2B7FFF))
            VerticalDivider()
            StatusItem("Selesai", doneMission, R.drawable.done, Color(0xff00C950))
        }
    }
}

@Composable
private fun StatusItem(title: String, value: String, icon: Int, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painterResource(icon),
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(title, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MissionCard(
    modifier: Modifier,
    onExploreClick: () -> Unit,
    judul: String,
    level: String,
    alamat1: String,
    alamat2: String,
    misi: String,
    poin: Int,
    isCompleted: Boolean,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, Color(0xffE5E7EB)),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(judul, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Card(
                    colors = CardDefaults.cardColors(Color(0xffDCFCE7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        level,
                        modifier = Modifier.padding(horizontal = 6.dp),
                        color = Color(0xff008236)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row {
                Icon(
                    painterResource(R.drawable.location),
                    contentDescription = "Location",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Column {
                    Text(alamat1)
                    Text(alamat2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(misi, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.oin),
                        contentDescription = "Poin",
                        tint = Color(0xffF0B100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("+$poin poin", color = Color(0xff9810FA))
                }

                Button(
                    onClick = onExploreClick,
                    enabled = !isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        if (isCompleted) Color.Gray else Color(0xff3798F7)
                    )
                ) {
                    Text(if (isCompleted) "Selesai" else "Explore")
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }
        }
    }
}
