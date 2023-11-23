package com.fake.soundremote.ui.keystrokelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fake.soundremote.data.KeystrokeRepository
import com.fake.soundremote.data.Order
import com.fake.soundremote.util.generateDescription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KeystrokeUIState(
    val id: Int,
    val name: String,
    val description: String,
    val favoured: Boolean
)

data class KeystrokeListUIState(
    val keystrokes: List<KeystrokeUIState> = emptyList()
)

@HiltViewModel
class KeystrokeListViewModel @Inject constructor(
    private val keystrokeRepository: KeystrokeRepository,
) : ViewModel() {

    private val _keystrokeListState = MutableStateFlow(KeystrokeListUIState())
    val keystrokeListState: StateFlow<KeystrokeListUIState>
        get() = _keystrokeListState

    init {
        viewModelScope.launch {
            keystrokeRepository.getAllOrdered().collect { keystrokeList ->
                val keystrokes = keystrokeList.map { keystroke ->
                    KeystrokeUIState(
                        keystroke.id,
                        keystroke.name,
                        description = generateDescription(keystroke),
                        favoured = keystroke.isFavoured
                    )
                }
                _keystrokeListState.value = KeystrokeListUIState(keystrokes)
            }
        }
    }

    fun moveKeystroke(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val orderedIds = keystrokeListState.value.keystrokes.map { it.id }.toMutableList()
            require(fromIndex in orderedIds.indices && toIndex in orderedIds.indices) { "Invalid indices" }
            orderedIds.add(toIndex, orderedIds.removeAt(fromIndex))
            val orders = orderedIds.mapIndexed { index, id -> Order(id, orderedIds.size - index) }
            keystrokeRepository.updateOrders(orders)
        }
    }

    fun deleteKeystroke(id: Int) {
        viewModelScope.launch {
            keystrokeRepository.deleteById(id)
        }
    }

    fun changeFavoured(keystrokeId: Int, favoured: Boolean) {
        viewModelScope.launch {
            keystrokeRepository.changeFavoured(keystrokeId, favoured)
        }
    }
}
