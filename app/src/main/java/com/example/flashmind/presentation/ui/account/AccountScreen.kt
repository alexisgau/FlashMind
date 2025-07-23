package com.example.flashmind.presentation.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.presentation.viewmodel.AuthViewModel


@Composable
fun AccountSettingsScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    userData: String,
    navigateToLogin: () -> Unit
) {


    AccountScreenImpl(modifier = Modifier, userData, navigateToLogin, viewModel)

}


@Composable
fun AccountScreenImpl(
    modifier: Modifier,
    userData: String,
    navigateToLogin: () -> Unit,
    viewModel: AuthViewModel
) {

    val signOutState by viewModel.signOutState.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    LaunchedEffect(signOutState) {
        if (signOutState is AuthResponse.Success) {
            navigateToLogin()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "Account settings",
                style = MaterialTheme.typography.headlineSmall,
                color =  MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))

            AsyncImage(
                model = userData,
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(140.dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))

            Text("Preferences", style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.inverseSurface,)
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Modo oscuro",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text("DARK MODE", style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.inverseSurface)
                Spacer(Modifier.weight(1f))
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) })
            }

            Spacer(Modifier.height(32.dp))

            Text("Account", style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.inverseSurface,)
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.signOutWithGoogle()


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Sign out")
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Versión de la app: 1.0.0", style = MaterialTheme.typography.bodySmall, color =  MaterialTheme.colorScheme.inverseSurface)
            Spacer(Modifier.height(4.dp))
            Text("Envíanos tu feedback", style = MaterialTheme.typography.bodySmall, color =  MaterialTheme.colorScheme.inverseSurface,)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "Account settings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = null,
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(140.dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))

            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Modo oscuro",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text("DARK MODE", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = { })
            }

            Spacer(Modifier.height(32.dp))

            Text("Account", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Sign out")
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Versión de la app: 1.0.0", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text("Envíanos tu feedback", style = MaterialTheme.typography.bodySmall)
        }
    }
}



