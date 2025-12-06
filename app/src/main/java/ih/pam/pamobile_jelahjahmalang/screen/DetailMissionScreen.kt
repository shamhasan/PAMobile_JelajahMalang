package ih.pam.pamobile_jelahjahmalang.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jelajahmalang.viewmodel.MissionViewModel
import ih.pam.pamobile_jelahjahmalang.R
import ih.pam.pamobile_jelahjahmalang.R.drawable.location
import ih.pam.pamobile_jelahjahmalang.viewModel.DetailMissionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMissionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    placeId: String,
    viewModel: DetailMissionViewModel = viewModel(),
    missionTitle: String,
) {

    LaunchedEffect(placeId) {
        viewModel.getPlaceDetail(placeId = placeId)
    }

    val place by viewModel.placeDetail.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()


    Scaffold(
        containerColor = Color(0xffF4F8FF), topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isLoading) "Memuat data.." else "Detail Misi",
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
                    .height(250.dp)
            ) {
                Column(modifier = modifier.padding(18.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(missionTitle, fontSize = 22.sp, fontWeight = FontWeight.Medium)

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
                            Text(place!!.name, fontSize = 20.sp)
                            Text(place!!.address, fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Card(
                            onClick = {
                                print("Menuju Map!")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = modifier
                                .width(35.dp)
                                .height(35.dp),
                            border = BorderStroke(3.dp, Color(0xff1D8EFD)),
                            colors = CardDefaults.cardColors(Color.White)

                        ) {
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
                    }
                    Spacer(Modifier.height(8.dp))
                    Card(
                        border = BorderStroke(1.dp, Color(0xffB9F8CF)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(Color(0xffDCFCE7))
                    ) {
                        Text(
                            "easy",
                            modifier.padding(horizontal = 5.dp),
                            color = Color(0xff008236)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Ambil foto di depan Monumen Nasional yang ikonik sebagai bukti kunjungan Anda.")
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
                            Text("+100 poin")
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
                    Button(
                        onClick = {},
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
                        Text("Buka Kamera")
                    }
                    Button(
                        onClick = {},
                        modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 0.dp)
                            .shadow(shape = RoundedCornerShape(12.dp), elevation = 0.01.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Pilih dari galeri")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xff3798F7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Selesaikan Misi")
            }
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