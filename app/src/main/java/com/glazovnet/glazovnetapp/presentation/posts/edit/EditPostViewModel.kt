package com.glazovnet.glazovnetapp.presentation.posts.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.graphics.decodeBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.data.entity.utils.ImageModelDto
import com.glazovnet.glazovnetapp.domain.models.posts.PostModel
import com.glazovnet.glazovnetapp.domain.usecase.PostsUseCase
import com.glazovnet.glazovnetapp.domain.usecase.UtilsUseCase
import com.glazovnet.glazovnetapp.domain.utils.Resource
import com.glazovnet.glazovnetapp.presentation.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val utilsUseCase: UtilsUseCase,
    private val postsUseCase: PostsUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ScreenState<PostModel>())
    val state = _state.asStateFlow()
    private val _messageStringResource = Channel<Int>()
    val messageStringResource = _messageStringResource.receiveAsFlow()

    private var _postTitle = MutableStateFlow("")
    val postTitle = _postTitle.asStateFlow()
    private var _postText = MutableStateFlow("")
    val postText = _postText.asStateFlow()
    private var _postImageUri = MutableStateFlow<Uri?>(null)
    val postImageUri = _postImageUri.asStateFlow()

    fun loadPostData(postId: String?) {
        if (postId !== null) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, stringResourceId = null, message = null) }
                when (val result = postsUseCase.getPostById(postId)) {
                    is Resource.Success -> {
                        _state.update { it.copy(data = result.data) }
                        _postTitle.update { result.data?.title ?: "" }
                        _postText.update { result.data?.text ?: "" }
                        _postImageUri.update { result.data?.image?.imageUrl?.toUri() }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(stringResourceId = result.stringResourceId, message = result.message)
                        }
                    }
                }
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updatePostTitle(title: String) {
        _postTitle.update { title }
    }
    fun updatePostText(text: String) {
        _postText.update { text }
    }
    fun updatePostImageUri(uri: Uri?) {
        _postImageUri.update { uri }
    }

    fun uploadPost(context: Context) {
        if (state.value.data != null) {
            updatePost(context, postTitle.value, postText.value, postImageUri.value)
        } else {
            addNewPost(context, postTitle.value, postText.value, postImageUri.value)
        }
    }

    private fun updatePost(
        context: Context,
        postTitle: String,
        postText: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }
            val isNeedToUpdateImage = state.value.data!!.image?.imageUrl !== imageUri.toString()
            val image = if (isNeedToUpdateImage) {
                if (imageUri !== null) uploadImageToServer(context, imageUri)
                else null
            } else state.value.data!!.image
            if ((imageUri !== null) == (image !== null)) {
                val postToUpload = with(state.value.data!!) {
                    PostModel(
                        id, postTitle, creationDateTime, postText, image
                    )
                }
                when (val result = postsUseCase.updatePost(postToUpload)) {
                    is Resource.Success -> {
                        _messageStringResource.send(R.string.edit_post_add_result_success)
                    }
                    is Resource.Error -> {
                        _messageStringResource.send(result.stringResourceId!!)
                    }
                }
            } else {
                _messageStringResource.send(R.string.edit_post_result_image_failed)
            }
            _state.update { it.copy(isUploading = false) }
        }
    }

    private fun addNewPost(
        context: Context,
        postTitle: String,
        postText: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isUploading = true)
            }
            val image = if (imageUri != null) uploadImageToServer(context, imageUri)
            else null
            if ((imageUri !== null) == (image !== null)) {
                val postToUpload = PostModel(
                    title = postTitle,
                    text = postText,
                    image = image
                )
                when (val result = postsUseCase.addPost(postToUpload)) {
                    is Resource.Success -> {
                        _messageStringResource.send(R.string.edit_post_add_result_success)
                    }
                    is Resource.Error -> {
                        _messageStringResource.send(result.stringResourceId!!)
                    }
                }
            } else {
                _messageStringResource.send(R.string.edit_post_result_image_failed)
            }
            _state.update { it.copy(isUploading = false) }
        }
    }

    private suspend fun uploadImageToServer(context: Context, uri: Uri): ImageModelDto? {
        val fileBites = context.contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
        val fileName = getImageName(uri.toString())
        val file = File(context.cacheDir, fileName)
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use {
                it.write(fileBites)
            }
        }
        val image = getCompressedImage(file)
        val imageModel = when (val uploadResult = utilsUseCase.uploadFile(file)) {
            is Resource.Success -> {
                val imageUrl = uploadResult.data!!.singleOrNull()
                if (imageUrl != null) {
                    ImageModelDto(
                        imageUrl = imageUrl,
                        imageWidth = image.width,
                        imageHeight = image.height
                    )
                } else null
            }
            is Resource.Error -> null
        }
        image.recycle()
        file.delete()
        return imageModel
    }

    private fun getImageName(filePath: String): String {
        val fileNameTrimCount = if (filePath.contains('%')) filePath.reversed().indexOf("%")
        else filePath.reversed().indexOf("/")
        var fileName = filePath.takeLast(fileNameTrimCount)
        val fileExtension = File(fileName).extension
        if (fileExtension.isBlank()) fileName += ".jpg"
        return fileName
    }

    private fun getCompressedImage(
        file: File,
        maxDimensionSize: Float = 1920f,
        targetImageSizeKb: Int = 250
    ): Bitmap {
        var image = ImageDecoder.createSource(file).decodeBitmap { _, _ ->  }
        val maxDimension = maxOf(image.height, image.width)
        if (maxDimension > maxDimensionSize) {
            val scaleFactor = maxDimensionSize / maxDimension
            val newImageWidth = (image.width * scaleFactor).roundToInt()
            val newImageHeight = (image.height * scaleFactor).roundToInt()
            image = image.scale(newImageWidth, newImageHeight)
        }
        val imageSizeInKb = image.byteCount / 1024
        val compressFactor = max(a = ( targetImageSizeKb / imageSizeInKb * 100 ), b = 100)
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, compressFactor, it)
        }
        return image
    }
}