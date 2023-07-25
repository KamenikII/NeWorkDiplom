package ru.netology.neworkedin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.netology.neworkedin.dataclass.*
import ru.netology.neworkedin.repository.Repository
import javax.inject.Inject

/** КЛАСС ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ, ОБРАБОТКИ ИЗМЕНЕНИЙ, ЛОВЛЯ ОШИБОК */

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,

) : AndroidViewModel(application) {
    val dataUsersList
        get() = flow {
            while (true) {
                getData()
                emit(_dataUsersList)
                delay(1_000)
            }
        }

    private var _dataUsersList: List<User> = listOf()

    private fun getData() = viewModelScope.launch  {
        try {
            repository.getUsers()
        } catch (_: Exception) { }

        repository.dataUsers.collectLatest {
            _dataUsersList = it
        }
    }
}