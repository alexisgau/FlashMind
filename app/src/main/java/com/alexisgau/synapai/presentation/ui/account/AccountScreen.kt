package com.alexisgau.synapai.presentation.ui.account

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alexisgau.synapai.R
import com.alexisgau.synapai.domain.model.AuthResponse
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import androidx.core.net.toUri


@Composable
fun AccountSettingsScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
) {


    AccountScreenImpl(modifier = Modifier, navigateToLogin, navigateToHome, viewModel)

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenImpl(
    modifier: Modifier,
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    viewModel: AccountViewModel,
) {
    val signOutState by viewModel.signOutState.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    var showGuestLogoutDialog by remember { mutableStateOf(false) }
    val currentImageUrl by viewModel.currentImageUrl.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploadingPhoto.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val deleteState by viewModel.deleteAccountState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isAnonymous = viewModel.isUserAnonymous

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.updateProfilePicture(uri) }
    )

    LaunchedEffect(signOutState) {
        if (signOutState is AuthResponse.Success) navigateToLogin()
    }

    LaunchedEffect(deleteState) {
        deleteState?.onSuccess {
            Toast.makeText(
                context,
                context.getString(R.string.account_deleted_success),
                Toast.LENGTH_SHORT
            ).show()
            navigateToLogin()
        }?.onFailure { e ->
            if (
                e is FirebaseAuthRecentLoginRequiredException ||
                e.message?.contains("sensitive") == true
            ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.relogin_required),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.signOut()
                navigateToLogin()
            } else {
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.generic_error,
                        e.message ?: ""
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.account_settings_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navigateToHome() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
            )
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // FOTO DE PERFIL
                Box(contentAlignment = Alignment.Center) {
                    val imageModifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .then(if (!isAnonymous) Modifier.clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) } else Modifier)

                    if (currentImageUrl.isNotEmpty()) {
                        AsyncImage(model = currentImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = imageModifier,placeholder = painterResource(R.drawable.default_profile_ic))
                    } else {
                        Image(painter = painterResource(R.drawable.default_profile_ic), contentDescription = null, contentScale = ContentScale.Crop, modifier = imageModifier)
                    }

                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp)
                    }

                    if (!isAnonymous) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(6.dp).size(16.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))

                // PREFERENCIAS
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.account_preferences_section), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.inverseSurface)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.width(12.dp))
                        Text(text = stringResource(id = R.string.account_dark_mode_label).uppercase(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.inverseSurface)
                        Spacer(Modifier.weight(1f))
                        Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) })
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ACCIONES DE CUENTA
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.account_section), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.inverseSurface)
                    Spacer(Modifier.height(12.dp))

                    if (isAnonymous) {
                        Button(onClick = { navigateToLogin() }, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text(stringResource(R.string.create_account)) }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { showGuestLogoutDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text(stringResource(R.string.account_sign_out)) }
                    } else {
                        Button(onClick = { viewModel.signOut() }, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text(stringResource(id = R.string.account_sign_out)) }
                    }
                }

                // BOTÓN ELIMINAR CUENTA
                if (!isAnonymous) {
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.delete_account))
                    }
                }
            }


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                // Link de Privacidad (Recomendado por políticas)
                val privacyUrl = "https://sites.google.com/view/privacy-policy-synapai"
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
                        context.startActivity(intent)
                    }
                )

                Spacer(Modifier.height(8.dp))

                Text(text = stringResource(id = R.string.account_app_version), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.inverseSurface)
                Spacer(Modifier.height(4.dp))
                Text(text = stringResource(id = R.string.account_feedback), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.inverseSurface)
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.are_you_sure_title)) },
            text = { Text(stringResource(R.string.delete_account_text)) },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; viewModel.deleteAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.delete_all_and_exit)) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    if (showGuestLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showGuestLogoutDialog = false },
            title = { Text(stringResource(R.string.are_you_sure_title)) },
            text = { Text(stringResource(R.string.guest_mode_warning_message)) },
            confirmButton = {
                TextButton(
                    onClick = { showGuestLogoutDialog = false; viewModel.signOut() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.delete_all_and_exit)) }
            },
            dismissButton = { TextButton(onClick = { showGuestLogoutDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
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
                text = stringResource(id = R.string.account_settings_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = null,
                contentDescription = stringResource(id = R.string.account_profile_picture_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(140.dp)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.account_preferences_section),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(id = R.string.account_dark_mode_label),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(id = R.string.account_dark_mode_label).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = { })
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.account_section),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(stringResource(id = R.string.account_sign_out))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.account_app_version),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.account_feedback),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


