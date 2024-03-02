package com.glazovnet.glazovnetapp.posts.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier,
    post: PostModel,
    onClick: () -> Unit
) {
    val descriptionMaxLines = if (post.image == null) 10
    else 2
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onClick.invoke()
            }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            softWrap = true,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
//            text = post.creationDateTime!!.getLocalizedOffsetString(),
            text = buildAnnotatedString {
                if (post.lastEditDate != null) {
                    append(stringResource(id = R.string.post_details_screen_updated_text_long) + " | ")
                }
                append(post.creationDateTime!!.getLocalizedOffsetString())
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
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
        if (post.image != null) {
            val imageAspectRatio = post.image.imageWidth.toFloat() / post.image.imageHeight.toFloat()
            AsyncImage(
                model = post.image.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Medium
            )
        }
    }
}