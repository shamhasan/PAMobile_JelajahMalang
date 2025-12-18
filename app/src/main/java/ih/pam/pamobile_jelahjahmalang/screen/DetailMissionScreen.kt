package ih.pam.pamobile_jelahjahmalang.screen

import android.Manifest
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import ih.pam.pamobile_jelahjahmalang.R
import ih.pam.pamobile_jelahjahmalang.viewmodel.MissionViewmodel
import ih.pam.pamobile_jelahjahmalang.viewmodel.UserViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMissionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    missionId: String,
) {
    val context = LocalContext.current

    val vmMission: MissionViewmodel = viewModel()
    val vmUser: UserViewmodel = viewModel()

    val missions by vmMission.missions.collectAsState()
    val authStatus by vmUser.authStatus.collectAsState()

    val selectedMission = remember(missions, missionId) {
        missions.find { it.getCleanId() == missionId }
    }

    val fusedLocationClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            capturedImage = it
            Toast.makeText(context, "Foto berhasil diambil", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authStatus) {
        when (authStatus) {
            "mission_completed" -> {
                Toast.makeText(context, "Misi selesai! Poin bertambah 🎉", Toast.LENGTH_LONG).show()
                navController.popBackStack()
                vmUser.resetAuthStatus()
            }

            "mission_already_completed" -> {
                Toast.makeText(context, "Misi sudah pernah diselesaikan", Toast.LENGTH_SHORT).show()
                vmUser.resetAuthStatus()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xffF4F8FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detail Misi",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        Column(
            modifier
                .padding(padding)
                .padding(24.dp)
        ) {

            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xffE5E7EB)),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        text = selectedMission?.fields?.title?.value ?: "-",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.location),
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                selectedMission?.fields?.locationName?.value ?: "-",
                                fontSize = 16.sp
                            )
                            Text(
                                selectedMission?.fields?.address?.value ?: "-",
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        selectedMission?.fields?.missionTask?.value ?: "-",
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    capturedImage?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Captured",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xff3798F7))
                    ) {
                        Text("Ambil Foto Misi")
                    }
                }
            }
        }
    }
}
