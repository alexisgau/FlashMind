package com.example.flashmind.presentation.ui.register


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    navigateBackToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(id = R.string.create_account),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text(stringResource(id = R.string.auth_email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = viewModel.emailError != null,
                    supportingText = {
                        viewModel.emailError?.let { msgId ->
                            Text(
                                stringResource(id = msgId),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text(stringResource(id = R.string.auth_password_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = viewModel.passwordError != null,
                    supportingText = {
                        viewModel.passwordError?.let { msgId ->
                            Text(
                                stringResource(id = msgId),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        val image =
                            if (isPasswordVisible) R.drawable.visibility_icon else R.drawable.visibility_off_icon
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- CAMPO CONFIRMAR CONTRASEÑA ---
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar contraseña") }, // Asegúrate de tener stringResource si prefieres
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = viewModel.confirmPasswordError != null,
                    supportingText = {
                        viewModel.confirmPasswordError?.let { msgId ->
                            Text(
                                stringResource(id = msgId),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.register()
                        }
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        val image =
                            if (isConfirmPasswordVisible) R.drawable.visibility_icon else R.drawable.visibility_off_icon
                        IconButton(onClick = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN REGISTRAR
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.register()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState !is RegisterUiState.Loading
                ) {
                    if (uiState is RegisterUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.auth_sign_up),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (uiState is RegisterUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as RegisterUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //  Ir al Login
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.auth_already_have_account_prompt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.auth_sign_in),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navigateBackToLogin() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}