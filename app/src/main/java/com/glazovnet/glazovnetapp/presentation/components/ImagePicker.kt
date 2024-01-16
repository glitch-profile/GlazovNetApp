package com.glazovnet.glazovnetapp.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.glazovnet.glazovnetapp.R
import java.io.File

const val CONTRACT = "image/*"

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onNewImageSelected: (uri: Uri?) -> Unit,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    var imageName by remember {
        mutableStateOf("")
    }
    var isImageSelected by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        onNewImageSelected.invoke(it)
    }

    LaunchedEffect(key1 = imageUri) {
        fun getImageName(filePath: String): String {
            val fileNameTrimCount = if (filePath.contains('%')) filePath.reversed().indexOf("%")
            else filePath.reversed().indexOf("/")
            var fileName = filePath.takeLast(fileNameTrimCount)
            val fileExtension = File(fileName).extension
            if (fileExtension.isBlank()) fileName += ".jpg"
            return fileName
        }

        if (imageUri !== null) {
            imageName = getImageName(imageUri.toString())
            isImageSelected = true
        } else isImageSelected = false
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = BorderStroke(1.dp, Color.LightGray),
        contentColor = contentColor
    ) {
        Crossfade(targetState = isImageSelected, label = "imagePickerStateTransition") {
            when (it) {
                true -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { launcher.launch(CONTRACT) },
                            model = imageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.Low
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                text = imageName
                            )
                            TextButton(
                                onClick = { onNewImageSelected.invoke(null) }
                            ) {
                                Text(text = stringResource(id = R.string.reusable_text_clear))
                            }
                        }
                    }
                }
                false -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { launcher.launch(CONTRACT) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                text = stringResource(id = R.string.comp_image_picker_label)
                            )
                        }
                    }
                }
            }
        }
    }
}