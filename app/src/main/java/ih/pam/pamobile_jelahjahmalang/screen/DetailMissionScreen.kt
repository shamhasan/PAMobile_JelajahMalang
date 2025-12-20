package ih.pam.pamobile_jelahjahmalang.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import ih.pam.pamobile_jelahjahmalang.R
import ih.pam.pamobile_jelahjahmalang.R.drawable.location
import ih.pam.pamobile_jelahjahmalang.viewmodel.MissionViewmodel
import ih.pam.pamobile_jelahjahmalang.viewmodel.UserViewmodel
import android.content.Intent
import android.net.Uri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMissionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    missionId: String,
) {
    val context = LocalContext.current // Context untuk Toast dan Location
    val vmMission: MissionViewmodel = viewModel()
    val vmUser: UserViewmodel = viewModel()

    val authStatus by vmUser.authStatus.collectAsState()
    val missions by vmMission.missions.collectAsState()

    val selectedMission = remember(missions, missionId) {
        missions.find { it.getCleanId() == missionId }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var isCheckingLocation by remember { mutableStateOf(false) }

    // Launcher untuk Izin Lokasi
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            Toast.makeText(context, "Izin lokasi diberikan. Tekan tombol lagi.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Izin lokasi wajib untuk verifikasi misi!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authStatus) {
        if (authStatus == "mission_completed") {
            // 1. Tampilkan Pesan Sukses
            Toast.makeText(navController.context, "Selamat! Poin Bertambah!", Toast.LENGTH_LONG).show()
            Log.d("LOG CONTEXT !!!", navController.context.toString())

            // 2. Kembali ke Halaman Sebelumnya (Home/List Misi)
            navController.popBackStack()

            // 3. Reset Status agar tidak terpanggil ulang
            vmUser.resetAuthStatus()
        } else if (authStatus == "mission_already_completed") {
            Toast.makeText(navController.context, "Misi ini sudah kamu selesaikan sebelumnya.", Toast.LENGTH_SHORT).show()
            vmUser.resetAuthStatus()
        }
    }

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            Toast.makeText(navController.context, "Foto berhasil diambil!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    val pickImageGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val bitmap =
                MediaStore.Images.Media.getBitmap(navController.context.contentResolver, uri)
            capturedImage = bitmap
            Toast.makeText(navController.context, "Foto berhasil diambil!", Toast.LENGTH_SHORT)
                .show()
        }

    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(navController.context, "Izin kamera ditolak!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color(0xffF4F8FF), topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Misi",
                        textAlign = TextAlign.Center,
                        modifier = modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        }) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(24.dp)
        )
        {
            Card(
                border = BorderStroke(1.1.dp, color = Color(0xffE5E7EB)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) {
                Column(modifier = modifier.padding(18.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(
                            selectedMission?.fields?.title?.value.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        )

                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        Icon(
                            painter = painterResource(id = location),
                            contentDescription = "Location On",
                            modifier.requiredSize(20.dp),
                            tint = Color(0xffE5E7EB)
                        )
                        Spacer(
                            Modifier.width(
                                4.dp
                            )
                        )
                        Column {
                            Text(
                                selectedMission?.fields?.locationName?.value.toString(),
                                fontSize = 20.sp
                            )
                            Box(modifier.width(250.dp)) {
                                Text(
                                    selectedMission?.fields?.address?.value.toString(),
                                    fontSize = 12.sp,
                                    minLines = 2,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(0.dp))
                        Card(
                            onClick = {
                                // 1. Ambil koordinat (pastikan tidak null/0.0)
                                val lat = selectedMission?.fields?.latitude?.value ?: 0.0
                                val lng = selectedMission?.fields?.longitude?.value ?: 0.0
                                val label = Uri.encode(selectedMission?.fields?.title?.value ?: "Lokasi Misi")

                                // 2. Buat URI untuk Google Maps
                                // Format: "geo:<lat>,<lng>?q=<lat>,<lng>(<label>)"
                                // Parameter 'q' akan membuat pin (marker) di lokasi tersebut
                                val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")

                                // 3. Buat Intent
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps") // Paksa buka di App Google Maps

                                // 4. Jalankan Intent
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    // Fallback jika aplikasi Maps tidak terinstall (opsional: buka di browser)
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
                                    context.startActivity(browserIntent)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = modifier
                                .width(35.dp)
                                .height(35.dp),
                            border = BorderStroke(3.dp, Color(0xff1D8EFD)),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            // ... (konten icon tetap sama)
                            Spacer(Modifier.height(6.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = "",
                                modifier
                                    .requiredSize(25.dp)
                                    .align(alignment = Alignment.CenterHorizontally),
                                tint = Color(0xff1D8EFD)
                            )
                        }
//                        Card(
//                            onClick = {
//                                Log.d("Map Button", "Menuju map")
//                            },
//                            shape = RoundedCornerShape(8.dp),
//                            modifier = modifier
//                                .width(35.dp)
//                                .height(35.dp),
//                            border = BorderStroke(3.dp, Color(0xff1D8EFD)),
//                            colors = CardDefaults.cardColors(Color.White)
//
//                        ) {
//                            Spacer(Modifier.height(6.dp))
//                            Icon(
//                                painter = painterResource(id = R.drawable.map),
//                                contentDescription = "",
//                                modifier
//                                    .requiredSize(25.dp)
//                                    .align(alignment = Alignment.CenterHorizontally),
//                                tint = Color(0xff1D8EFD)
//
//                            )
//                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Card(
                        border = BorderStroke(1.dp, Color(0xffB9F8CF)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(Color(0xffDCFCE7))
                    ) {
                        Text(
                            selectedMission?.fields?.level?.value.toString(),
                            modifier.padding(horizontal = 5.dp),
                            color = Color(0xff008236)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(selectedMission?.fields?.missionTask?.value.toString())
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.oin),
                                contentDescription = "",
                                modifier
                                    .requiredSize(20.dp),
                                tint = Color(0xffF0B100)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("+" + selectedMission?.fields?.points?.value.toString() + " poin")
                        }


                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(
                modifier
                    .height(370.dp)
                    .fillMaxWidth()
                    .dashedBorder(strokeWidth = 7.dp, color = Color(0xffD6D6D6)),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(15.dp),
            ) {
                Column(
                    modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (capturedImage == null) {
                        Card(
                            shape = RoundedCornerShape(50.dp),
                            modifier = modifier.size(100.dp),
                            colors = CardDefaults.cardColors(Color(0xffB9D3FF))
                        ) {
                            Box(
                                modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painterResource(R.drawable.camera),
                                    contentDescription = "Camera",
                                    modifier = modifier.requiredSize(60.dp)
                                )
                            }
                        }

                        Text(
                            "Ambil Foto Bukti",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = modifier.padding(top = 10.dp)
                        )
                        Text(
                            "Foto akan digunakan sebagai bukti \n" +
                                    "penyelesaian misi",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            modifier = modifier.padding(10.dp)
                        )
                    } else {
                        Box(
                            modifier = modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                        ) {
                            Image(
                                bitmap = capturedImage!!.asImageBitmap(),
                                contentDescription = "Captured Image",
                                modifier = modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Button(
                        onClick = {
                            val permission = ContextCompat.checkSelfPermission(
                                navController.context,
                                Manifest.permission.CAMERA
                            )
                            if (permission == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }

                        },
                        modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 0.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.camera),
                            contentDescription = "Camera Button",
                            tint = Color.White,
                            modifier = modifier
                                .padding(horizontal = 8.dp)
                                .requiredSize(25.dp)
                        )
                        Text(if (capturedImage == null) "Buka Kamera" else "Foto Ulang")
                    }
                    Button(
                        onClick = {
                            pickImageGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 0.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Pilih dari galeri")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (capturedImage == null) {
                        Toast.makeText(context, "Harap ambil foto bukti terlebih dahulu!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val fineLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val coarseLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if (fineLocationPermission != PackageManager.PERMISSION_GRANTED &&
                        coarseLocationPermission != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        return@Button
                    }

                    Toast.makeText(context, "Mengecek lokasi...", Toast.LENGTH_SHORT).show()

                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        com.google.android.gms.tasks.CancellationTokenSource().token // Tambahkan Token
                    ).addOnSuccessListener { location ->
                        if (location != null) {
                            val targetLat = selectedMission?.fields?.latitude?.value ?: 0.0
                            val targetLng = selectedMission?.fields?.longitude?.value ?: 0.0

                            // Panggil fungsi validasi di ViewModel
                            val isValid = vmMission.validateMissionDistance(
                                userLat = location.latitude,
                                userLng = location.longitude,
                                missionLat = targetLat,
                                missionLng = targetLng
                            )

                            if (isValid) {
                                vmUser.completedMission(
                                    missionId = missionId,
                                    pointsToAdd = selectedMission?.fields?.points?.value?.toInt() ?: 0,
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Lokasi terlalu jauh dari target misi!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Lokasi null (biasanya jika GPS baru nyala dan belum lock)
                            Toast.makeText(context, "Gagal mendeteksi lokasi. Coba nyalakan GPS dulu.", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { e ->
                        // 4. Tangkap Error
                        Log.e("LocationError", "Error: ${e.message}")
                        Toast.makeText(context, "Error mengambil lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Selesaikan Misi")
            }
            //            Button(
//                onClick = {
//                    vmUser.completedMission(
//                        missionId = missionId,
//                        pointsToAdd = selectedMission?.fields?.points?.value?.toInt() ?: 0,
//                    )
//                },
//                modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Text("Selesaikan Misi")
//            }
        }
    }
}

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 10.dp,
    cornerRadius: Dp = 15.dp,
    dashLength: Dp = 10.dp, // Panjang garis
    gapLength: Dp = 10.dp,   // Panjang spasi kosong
) = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f
        )
    )

    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}