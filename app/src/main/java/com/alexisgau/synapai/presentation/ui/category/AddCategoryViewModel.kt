package com.alexisgau.synapai.presentation.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.usecase.category.InsertCategoryUseCase
import com.alexisgau.synapai.presentation.ui.home.AddCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(private val insertCategoryUseCase: InsertCategoryUseCase) :
    ViewModel() {

    private val _addCategoryState = MutableStateFlow<AddCategoryState>(AddCategoryState.Loading)
    val addCategoryState: StateFlow<AddCategoryState> = _addCategoryState.asStateFlow()

    fun insertCategory(category: Category) {
        _addCategoryState.value = AddCategoryState.Loading
        viewModelScope.launch {
            runCatching { insertCategoryUseCase(category) }
                .onSuccess { _addCategoryState.value = AddCategoryState.Success }
                .onFailure { e ->
                    _addCategoryState.value =
                        AddCategoryState.Error(e.message ?: "Error desconocido")
                }
        }
    }

}