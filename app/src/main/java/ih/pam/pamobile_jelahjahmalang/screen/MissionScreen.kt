package ih.pam.pamobile_jelahjahmalang.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.jelajahmalang.viewmodel.MissionViewModel
import ih.pam.pamobile_jelahjahmalang.R


@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun MissionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MissionViewModel = viewModel(),
) {
    val missionList by viewModel.missionList.collectAsState()
    val listState = rememberLazyListState()

    BoxWithConstraints {

        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight
        val isLandscape = screenWidth > screenHeight

        Scaffold(
            containerColor = Color(0xffF4F8FF), topBar = {
                TopAppBar(
                    title = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "Mission Explore",
                                    textAlign = TextAlign.Center,
                                    modifier = modifier
                                        .fillMaxWidth()
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back Button"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xffF4F8FF)
                            ),
                            )
                    },
                    )
            }) { innerPadding ->
            if (!isLandscape) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ), modifier = modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    )
                        ) {
                            Row(
                                modifier = modifier.padding(vertical = 18.dp)
                            ) {
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        tint = Color(0xffF0B100),
                                        painter = painterResource(id = R.drawable.oin),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp)
                                    )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                                VerticalDivider()
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.done),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp),
                                        tint = Color(0xff00C950)
                                    )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                                VerticalDivider()
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.available),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp),
                                        tint = Color(0xff2B7FFF)
                                   )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                            }
                        }
                        // Bar status end
                        Spacer(Modifier.height(38.dp))
                        // List misi start
                        Text(
                            text = "Daftar Misi",
                            textAlign = TextAlign.Start,
                            modifier = modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    // Bar status start
                   items(missionList.size) { e ->
                       val mission = missionList[e]

                        MissionCard(
                            modifier = Modifier.padding(),
                            onExploreClick = { navController.navigate("detail_mission/${mission.placeId}/${mission.title}") },
                            judul = mission.title,
                            level = mission.level,
                            alamat1 = mission.locationName,
                            alamat2 = mission.placeDetailAddress,
                            misi = mission.missionTask,
                            poin = mission.points,
                            )
                        Spacer(Modifier.height(8.dp))

                    }
                    // List Misi End
                }

            } else {
                LazyRow(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    state = listState
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ), modifier = modifier
                                .fillMaxWidth()
                                .height(125.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    )
                        ) {
                            Row(
                                modifier = modifier.padding(horizontal = 18.dp)
                            ) {
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        tint = Color(0xffF0B100),
                                        painter = painterResource(id = R.drawable.oin),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp)
                                    )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                                VerticalDivider()
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.done),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp),
                                        tint = Color(0xff00C950)
                                    )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                                VerticalDivider()
                                Column(
                                    modifier = modifier
                                        .width(125.dp)
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.available),
                                        contentDescription = "Done icon",
                                        modifier = modifier.requiredSize(25.dp),
                                        tint = Color(0xff2B7FFF)
                                    )
                                    Text(text = "Total Poin")
                                    Text(text = "0")
                                }
                            }
                        }
                        // Bar status end
                        Spacer(Modifier.height(38.dp))
                        // List misi start
                        Text(
                            text = "Daftar Misi",
                            textAlign = TextAlign.Start,
                            modifier = modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    // Bar status start
                    items(missionList.size) { e ->
                        val mission = missionList[e]

                        MissionCard(
                            modifier = Modifier.padding(),
                            onExploreClick = { navController.navigate("detail_mission/${mission.placeId}") },
                            judul = mission.title,
                            level = mission.level,
                            alamat1 = mission.locationName,
                            alamat2 = mission.placeDetailAddress,
                            misi = mission.missionTask,
                            poin = mission.points,
                        )
                        Spacer(Modifier.width(8.dp))

                    }
                }
            }
        }
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
) {
    Card(
        border = BorderStroke(1.1.dp, color = Color(0xffE5E7EB)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        Column(modifier = modifier.padding(18.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = judul,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Card(
                    border = BorderStroke(1.dp, Color(0xffB9F8CF)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(Color(0xffDCFCE7))
                ) {
                    Text(
                        level,
                        modifier.padding(horizontal = 5.dp),
                        color = Color(0xff008236)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = "Location On",
                    tint = Color(0xffE5E7EB),
                    modifier = modifier.requiredSize(20.dp)
                )
                Column {
                    Text(alamat1, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(alamat2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(misi, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.oin),
                        contentDescription = "poin",
                        modifier.requiredSize(20.dp),
                        tint = Color(0xffF0B100)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("+" + poin + " poin", color = Color(0xff9810FA))
                }
                Button(
                    onExploreClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(0xff3798F7),
                    ),
                    modifier = modifier.width(120.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.width(120.dp)
                    ) {
                        Text("Explore")
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}