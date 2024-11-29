package com.marcusdoucette.fridgefreshness.views.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import com.marcusdoucette.fridgefreshness.FridgeItemData
import com.marcusdoucette.fridgefreshness.R
import kotlinx.coroutines.flow.update


data class NewFridgeItem(
    val image: Bitmap? = null,
    val name: String? = null,
    val next_check: LocalDate? = null
)

class NewItemViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewFridgeItem())
    val uiState: StateFlow<NewFridgeItem> = _uiState.asStateFlow()

    fun getValidatedState(default_image:Bitmap): FridgeItemData? {
        return _uiState.value.name?.let { name ->
            _uiState.value.next_check?.let { next ->
                FridgeItemData(image = _uiState.value.image ?: default_image,
                    name = name,
                    next_reccomended_check = next)
            }
        }
    }

    fun resetState(){
        _uiState.update{currentState->
            currentState.copy(
                name=null,
                image=null,
                next_check=null
            )
        }
    }

    fun changeName(it: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = it
            )
        }
    }

    fun setDate(it: LocalDate) {
        _uiState.update { currentState ->
            currentState.copy(
                next_check = it
            )
        }
    }

    fun setBM(it: Bitmap){
        _uiState.update{ currentState ->
            currentState.copy(
                image = it
            )
        }
    }

}