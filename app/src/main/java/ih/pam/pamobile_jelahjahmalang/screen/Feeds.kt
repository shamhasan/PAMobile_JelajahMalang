package ih.pam.pamobile_jelahjahmalang.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ih.pam.pamobile_jelahjahmalang.model.PlaceModel
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    vm: PlaceViewModel
) {
    val places by vm.places.collectAsState()
    val favorites by vm.favorites.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    // data favorit berdasarkan nama
    val favoritePlaces = places.filter { favorites.contains(it.name) }
    val recommended by vm.recommended.collectAsState()


    LaunchedEffect(Unit) {
        vm.getPlaces()
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F9FF))
                .padding(padding)
        ) {

            // ===== HEADER BIRU =====
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF4BA3FF))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Text(
                            "Temukan sisi lain Kota Malang lewat rekomendasi warga lokal.",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height( 8.dp))
                        Text(
                            "Mulai dari tempat nongkrong tersembunyi sampai spot foto estetik — semua ada di sini!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ===== 2 KARTU ATAS: MISSION & SEKITAR ANDA =====
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TopMenuCard(
                        title = "Mission",
                        emoji = "🔥",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate("mission_list")
                            Log.d("TopMenuCard", "Mission")
                        }
                    )
                    TopMenuCard(
                        title = "Sekitar Anda",
                        emoji = "📍",
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

//             ===== SECTION FAVORIT =====
            item {
                Text(
                    text = "Favorit",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(4.dp))

                if (favoritePlaces.isEmpty()) {
                    Text(
                        text = "Belum ada favorit",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoritePlaces) { place ->
                            PlaceCardItem(
                                place = place,
                                modifier = Modifier.width(180.dp)
                            ) {
                                navController.navigate("detail/${place.name}")
                            }

                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
            if (recommended.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Rekomendasi untuk Kamu",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommended) { place ->
                            PlaceCardItem(
                                place = place,
                                modifier = Modifier.width(180.dp)
                            ) {
                                navController.navigate("detail/${place.name}")
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }


//            ===== LOADING / ERROR =====
            if (loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (error != null) {
                item {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }


// FEEDS!!!!!!!!!!!
            // Grid 2 kolom, scroll ke bawah
            item {
                Text(
                    "List Tempat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )
            }
            items(places.chunked(2)) { rowPlaces ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Kolom kiri
                    PlaceCardItem(
                        place = rowPlaces[0],
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate("detail/${rowPlaces[0].name}")
                    }

                    // Kolom kanan (kalau ada)
                    if (rowPlaces.size > 1) {
                        PlaceCardItem(
                            place = rowPlaces[1],
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate("detail/${rowPlaces[1].name}")
                        }
                    } else {
                        // Biar row tetap rata kalau cuma 1 item
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
fun TopMenuCard(
    title: String,
    onClick: () -> Unit ,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = {
            onClick()
        }
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(Modifier.height(6.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun PlaceCardItem(
    place: PlaceModel,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 110.dp,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(place.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("${place.rating} ⭐", fontSize = 12.sp)
            }
        }
    }
}
