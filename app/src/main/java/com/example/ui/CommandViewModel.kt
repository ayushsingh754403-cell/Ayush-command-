package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommandEntity
import com.example.network.callGeminiApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CommandViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).commandDao()

    val savedCommands: StateFlow<List<CommandEntity>> = dao.getAllCommands()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCommands: StateFlow<List<CommandEntity>> = dao.getFavoriteCommands()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentResult = MutableStateFlow<CommandEntity?>(null)
    val currentResult: StateFlow<CommandEntity?> = _currentResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun generateCommands(query: String, category: String = "General") {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val fullPrompt = "Task/Goal: $query. Category: $category. Provide the exact Termux/Linux commands line-by-line with a clear title and instructions."
                val rawAiResponse = callGeminiApi(fullPrompt)

                // Parse or structure response
                val lines = rawAiResponse.lines()
                val title = if (lines.isNotEmpty() && lines[0].isNotBlank()) {
                    lines[0].removePrefix("#").removePrefix("*").trim()
                } else {
                    query
                }

                val description = if (lines.size > 1) {
                    lines.drop(1).takeWhile { !it.contains("`") && it.isNotBlank() }.joinToString(" ")
                } else {
                    "Generated Termux commands for: $query"
                }

                val entity = CommandEntity(
                    query = query,
                    title = title.ifBlank { query },
                    description = description.ifBlank { "Line-by-line Termux commands" },
                    commandsText = rawAiResponse,
                    category = category
                )

                val id = dao.insertCommand(entity)
                val saved = dao.getCommandById(id) ?: entity.copy(id = id)
                _currentResult.value = saved
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to generate commands. Please check API key."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCommand(command: CommandEntity) {
        _currentResult.value = command
    }

    fun toggleFavorite(command: CommandEntity) {
        viewModelScope.launch {
            val updated = command.copy(isFavorite = !command.isFavorite)
            dao.updateCommand(updated)
            if (_currentResult.value?.id == updated.id) {
                _currentResult.value = updated
            }
        }
    }

    fun deleteCommand(command: CommandEntity) {
        viewModelScope.launch {
            dao.deleteCommand(command)
            if (_currentResult.value?.id == command.id) {
                _currentResult.value = null
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.deleteAll()
            _currentResult.value = null
        }
    }
}
