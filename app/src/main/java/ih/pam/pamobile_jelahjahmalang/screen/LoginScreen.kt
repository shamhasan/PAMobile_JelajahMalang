package ih.pam.pamobile_jelahjahmalang.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ih.pam.pamobile_jelahjahmalang.viewmodel.UserViewmodel

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController, viewmodel: UserViewmodel = viewModel()) {

    var emailTextField: String by remember { mutableStateOf("") }
    var passwordTextField: String by remember { mutableStateOf("") }

    val authStatus by viewmodel.authStatus.collectAsState()

    LaunchedEffect(authStatus) {
        if (authStatus == "success") {
            // Jika status berubah jadi sukses -> Pindah halaman
            navController.navigate("mission_list") {
                // (Opsional) Hapus halaman register/login dari backstack agar tidak bisa back
                popUpTo("login") { inclusive = true }
            }
            viewmodel.resetAuthStatus() // Reset agar tidak terpanggil ulang
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Login Screen", style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            )
            OutlinedTextField(
                value = emailTextField,
                onValueChange = { x ->
                    emailTextField = x
                },
                label = { Text("Email") },
                modifier = modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = passwordTextField,
                onValueChange = {
                    passwordTextField = it
                },
                label = { Text("Password") },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = { viewmodel.loginUser(email = emailTextField, pass = passwordTextField)}
            ) {
                Text("Login")
            }
            TextButton(
                onClick = {
                    navController.navigate("register")
                }
            ) {
                Text("Register")
            }
        }
    }
}