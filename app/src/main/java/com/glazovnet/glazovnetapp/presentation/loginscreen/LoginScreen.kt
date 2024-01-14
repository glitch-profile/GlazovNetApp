package com.glazovnet.glazovnetapp.presentation.loginscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.presentation.components.DesignedCheckBox
import com.glazovnet.glazovnetapp.presentation.components.DesignedTextField

private const val SIDE_PADDING = 16

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState = viewModel.loginState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val imageUrl = viewModel.introImageUrl.collectAsState().value
        if (imageUrl.isNotEmpty()) {
            println(imageUrl)
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
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
    ) {
        val cardsBackgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        val errorCardBackgroundColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
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
                navController = navController
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
                viewModel = viewModel,
                navController = navController
            )
        }
        AnimatedVisibility(visible = ( loginState.message != null || loginState.stringResourceId != null )) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(SIDE_PADDING.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SIDE_PADDING.dp)
                        .background(
                            color = errorCardBackgroundColor,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    ErrorMessageScreen(
                        stringResourceMessage = loginState.stringResourceId,
                        message = loginState.message
                    )
                }

            }
        }
    }
}

@Composable
private fun AuthForm(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val username = viewModel.username.collectAsState().value
    val password = viewModel.password.collectAsState().value
    val isSaveAuthData = viewModel.saveAuthData.collectAsState().value

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
                        isAsAdmin = false,
                        onLoginSuccessfully = {
                            navController.navigate("posts-list-screen") //TODO
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
        Spacer(modifier = Modifier.height(4.dp))
        DesignedCheckBox(
            isChecked = isSaveAuthData,
            onStateChanges = { viewModel.editIsSaveAuthData(it) },
            label = stringResource(id = R.string.login_remember_user_text)
        )
    }
}

@Composable
private fun ActionButtonsForm(
    isButtonsEnabled: Boolean,
    navController: NavController,
    viewModel: LoginViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = {
                viewModel.login(
                    isAsAdmin = true,
                    onLoginSuccessfully = {
                        navController.navigate("posts-list-screen") //TODO
                    }
                )
            },
            enabled = isButtonsEnabled
        ) {
            Text(text = stringResource(id = R.string.login_screen_login_as_admin_button))
        }
        Button(
            onClick = { 
                viewModel.login(
                    isAsAdmin = false,
                    onLoginSuccessfully = {
                        navController.navigate("posts-list-screen") //TODO
                    }
                ) 
            },
            enabled = isButtonsEnabled
        ) {
            Text(text = stringResource(id = R.string.login_screen_login_as_user_button))
        }
    }
}

@Composable
private fun ErrorMessageScreen(
    stringResourceMessage: Int?,
    message: String?
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        if (stringResourceMessage != null) {
            Text(
                text = stringResource(id = stringResourceMessage),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 2
            )
        }
    }
}