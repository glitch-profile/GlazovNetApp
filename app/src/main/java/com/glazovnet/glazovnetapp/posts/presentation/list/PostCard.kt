package com.glazovnet.glazovnetapp.posts.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.domain.utils.getLocalizedOffsetString
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel

@Composable
fun PostCard(
    modifier : Modifier = Modifier,
    post: PostModel,
    onCardClicked: (postId: String) -> Unit
) {
    val descriptionMaxLines = if (post.image == null) 10
    else 2
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onCardClicked.invoke(post.id)
            }
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        if (post.image != null) {
//            val imageAspectRatio = post.image.imageWidth.toFloat() / post.image.imageHeight.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small),
                    model = post.image.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.Medium
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(8.dp),
                    text = buildAnnotatedString {
                        if (post.lastEditDate != null) {
                            append(stringResource(id = R.string.post_details_screen_updated_text_long) + " | ")
                        }
                        append(post.creationDateTime.getLocalizedOffsetString())
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
//        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            softWrap = true,
            maxLines = 2
        )
        if (post.image == null) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = buildAnnotatedString {
                    if (post.lastEditDate != null) {
                        append(stringResource(id = R.string.post_details_screen_updated_text_long) + " | ")
                    }
                    append(post.creationDateTime.getLocalizedOffsetString())
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = post.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = descriptionMaxLines
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}