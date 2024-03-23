package com.glazovnet.glazovnetapp.posts.presentation.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.decodeBitmap
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.core.data.utils.ImageModelDto
import com.glazovnet.glazovnetapp.core.domain.usecases.UtilsUseCase
import com.glazovnet.glazovnetapp.core.domain.utils.Resource
import com.glazovnet.glazovnetapp.core.presentation.ScreenState
import com.glazovnet.glazovnetapp.posts.domain.model.PostModel
import com.glazovnet.glazovnetapp.posts.domain.usecases.PostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import hilt_aggregated_deps._com_glazovnet_glazovnetapp_login_presentation_LoginViewModel_HiltModules_BindsModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.stream.Stream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
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
                        id = id,
                        title = postTitle,
                        text = postText,
                        creationDateTime = creationDateTime,
                        lastEditDate = null,
                        image = image
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
        targetImageSizeKb: Float = 300f
    ): Bitmap {
        var image = ImageDecoder.createSource(file).decodeBitmap { _, _ ->  }
        val maxDimension = maxOf(image.height, image.width)
        if (maxDimension > maxDimensionSize) {
            val scaleFactor = maxDimensionSize / maxDimension
            val newImageWidth = (image.width * scaleFactor).roundToInt()
            val newImageHeight = (image.height * scaleFactor).roundToInt()
            image = image.scale(newImageWidth, newImageHeight)
        }
        //base compression
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        val imageSizeInKb = ( file.length() / 1024 ).toInt()
        Log.i("Image_Compression", "getCompressedImage: image size - $imageSizeInKb")
        val compressFactor = min(a = ( targetImageSizeKb / imageSizeInKb * 100 ) * 1.8f, b = 100f).roundToInt()
        if (compressFactor < 100) {
            file.outputStream().use {
                image.compress(Bitmap.CompressFormat.JPEG, compressFactor, it)
            }
            Log.i("Image_Compression", "getCompressedImage: rewriting image, factor $compressFactor")
        }

        return image
    }
}