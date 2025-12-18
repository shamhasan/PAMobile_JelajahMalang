package ih.pam.pamobile_jelahjahmalang.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(placeId: String, navController: NavController, vm: PlaceViewModel) {

    val places by vm.places.collectAsState()
    val favorites by vm.favorites.collectAsState()
    val commentsMap by vm.comments.collectAsState()

    val place = places.find { it.name == placeId } ?: return
    val comments = commentsMap[place.name] ?: emptyList()
    val isFav = favorites.contains(place.name)

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(place.name) {
        vm.loadComments(place.name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray, RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(place.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("${place.rating}⭐", fontSize = 16.sp)

            Spacer(Modifier.height(12.dp))
            Text(place.description)

            IconButton(onClick = { vm.toggleFavorite(place.name) }) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFav) Color.Red else Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Komentar")

            if (comments.isEmpty()) {
                Text("Belum ada komentar", color = Color.Gray)
            } else {
                comments.forEach {
                    CommentCard("User", it.text, it.rating)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp) // samakan dengan padding Box
            ) {
                Text("Tambah Komentar")
            }
        }

        if (showDialog) {
            CommentDialog(
                placeName = place.name,
                onDismiss = { showDialog = false },
                onSubmit = { text, rating ->
                    vm.addComment(place.name, text, rating)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CommentCard(name: String, comment: String, rating: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text("⭐".repeat(rating))
            Text(comment)
        }
    }
}


@Composable
fun CommentDialog(
    placeName: String,
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Komentar untuk $placeName") },
        text = {
            Column {
                Row {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Komentar kamu") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
               onSubmit(text, rating)
            }) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

