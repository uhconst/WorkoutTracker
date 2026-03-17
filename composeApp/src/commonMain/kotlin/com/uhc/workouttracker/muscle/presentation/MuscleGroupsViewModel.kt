package com.uhc.workouttracker.muscle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MuscleGroupsViewModel(
    private val muscleGroupRepository: MuscleGroupRepository
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = muscleGroupRepository.observeMuscleGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editState = MutableStateFlow<EditState>(EditState.NotEditing)
    val editState = _editState.asStateFlow()

    fun addMuscleGroup(muscleName: String) {
        if (muscleName.isBlank()) return

        viewModelScope.launch {
            muscleGroupRepository.addMuscleGroup(muscleName)
        }
    }

    fun startEditing(muscleGroup: MuscleGroup) {
        _editState.update { EditState.Editing(muscleGroup) }
    }

    fun cancelEditing() {
        _editState.update { EditState.NotEditing }
    }

    fun updateMuscleGroup(newName: String) {
        val currentEditState = _editState.value
        if (currentEditState !is EditState.Editing || newName.isBlank()) return

        viewModelScope.launch {
            muscleGroupRepository.updateMuscleGroup(currentEditState.muscleGroup.copy(name = newName))
            _editState.update { EditState.NotEditing }
        }
    }

    fun deleteMuscleGroup(muscleGroup: MuscleGroup) {
        viewModelScope.launch {
            muscleGroupRepository.deleteMuscleGroup(muscleGroup.id)
        }
    }

    sealed class EditState {
        object NotEditing : EditState()
        data class Editing(val muscleGroup: MuscleGroup) : EditState()
    }
}
