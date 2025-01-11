package com.challenge.randomstringgeneratorapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.randomstringgenerator.model.RandomString
import com.challenge.randomstringgenerator.model.RandomStringRepository
import kotlinx.coroutines.launch

class RandomStringViewModel(private val repository: RandomStringRepository) : ViewModel() {

    private val _randomStrings = MutableLiveData<List<RandomString>>()
    val randomStrings: LiveData<List<RandomString>> = _randomStrings

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _randomStrings.value = emptyList()
    }

    fun fetchRandomString(maxLength: Int) {
        if (maxLength <= 0) {
            _errorMessage.value = "Max length must be greater than 0."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null  // Clear any previous error messages
        viewModelScope.launch {
            try {
                val result = repository.fetchRandomString(maxLength)
                _isLoading.value = false

                result.fold(
                    onSuccess = { randomString ->
                        if (randomString.value.isEmpty()) {
                            _errorMessage.value = "No data received. Please try again later."
                        } else {
                            _randomStrings.value = _randomStrings.value.orEmpty() + randomString
                        }
                    },
                    onFailure = { error ->
                        _errorMessage.value = error.localizedMessage
                    }
                )
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle unexpected errors such as network issues
                _errorMessage.value = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }

    fun deleteAllStrings() {
        _randomStrings.value = emptyList()
    }

    fun deleteString(index: Int) {
        _randomStrings.value = _randomStrings.value?.toMutableList()?.apply { removeAt(index) }
    }
}

