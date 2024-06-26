package com.glazovnet.glazovnetapp.login.presentation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.presentation.components.DesignedTextField
import com.glazovnet.glazovnetapp.core.presentation.components.MessageNotification

private const val SIDE_PADDING = 16

@Composable
fun LoginScreen(
    onNavigateToHomeScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState = viewModel.loginState.collectAsState().value
    val messageState = viewModel.messageState.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 45_000,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val imageUrl = viewModel.introImageUrl.collectAsState().value
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    },
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = SIDE_PADDING.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.Bottom
    ) {
        val cardsBackgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SIDE_PADDING.dp)
                .background(
                    color = cardsBackgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_glazov_net_logo),
                contentDescription = null)
        }
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(SIDE_PADDING.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SIDE_PADDING.dp)
                .background(
                    color = cardsBackgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            AuthForm(
                viewModel = viewModel,
                onNavigateToHomeScreen = onNavigateToHomeScreen
            )
        }
        Spacer(modifier = Modifier.height(SIDE_PADDING.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SIDE_PADDING.dp)
                .background(
                    color = cardsBackgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            ActionButtonsForm(
                isButtonsEnabled = !loginState.isLoading,
                onNavigateToHomeScreen = onNavigateToHomeScreen,
                viewModel = viewModel
            )
        }
    }
    MessageNotification(
        enabled = messageState.value.enabled,
        title = stringResource(id = messageState.value.titleResource),
        additionText = stringResource(id = messageState.value.additionTextResource)
    )
}

@Composable
private fun AuthForm(
    onNavigateToHomeScreen: () -> Unit,
    viewModel: LoginViewModel
) {
    val username = viewModel.username.collectAsState().value
    val password = viewModel.password.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        DesignedTextField(
            modifier = Modifier.fillMaxWidth(),
            text = username,
            onTextEdit = { viewModel.editUsername(it) },
            placeholder = stringResource(id = R.string.login_screen_username_text),
            keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(4.dp))
        DesignedTextField(
            modifier = Modifier.fillMaxWidth(),
            text = password,
            onTextEdit = { viewModel.editPassword(it) },
            placeholder = stringResource(id = R.string.login_screen_password_text),
            keyboardActions = KeyboardActions(
                onDone = { 
                    viewModel.login(
                        onLoginSuccessfully = {
                            onNavigateToHomeScreen.invoke()
                        }
                    ) 
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
private fun ActionButtonsForm(
    isButtonsEnabled: Boolean,
    onNavigateToHomeScreen: () -> Unit,
    viewModel: LoginViewModel
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        TextButton(
            modifier = Modifier
                .height(48.dp),
            shape = MaterialTheme.shapes.small,
            onClick = {
                viewModel.loginAsGuest(
                    onLoginSuccessfully = {
                        onNavigateToHomeScreen.invoke()
                    }
                )
            }
        ) {
            Text(text = stringResource(id = R.string.login_screen_login_as_guest_button))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = MaterialTheme.shapes.small,
            onClick = { 
                viewModel.login(
                    onLoginSuccessfully = {
                        onNavigateToHomeScreen.invoke()
                    }
                ) 
            },
            enabled = isButtonsEnabled
        ) {
            Text(text = stringResource(id = R.string.login_screen_login_as_user_button))
        }

    }
}