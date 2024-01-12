package com.fxyandtjh.voiceaccounting.viewmodel

import com.fxyandtjh.voiceaccounting.base.BaseViewModel
import com.fxyandtjh.voiceaccounting.entity.SecurityQuestion
import com.fxyandtjh.voiceaccounting.net.response.AccountSecurityMessage
import com.fxyandtjh.voiceaccounting.repository.impl.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AccountSecurityViewModel @Inject constructor(
   private val mainRepository: MainRepository
) : BaseViewModel() {

    private val securityMessage: MutableStateFlow<AccountSecurityMessage> = MutableStateFlow(
        AccountSecurityMessage(
            uid = 0,
            phoneNumber = "",
            name = "",
            registerTime = 0,
            securityQuestionId = -1
        )
    )
    val _securityMessage: StateFlow<AccountSecurityMessage> = securityMessage

    private val currentSelectID: MutableStateFlow<Int> = MutableStateFlow(-1)
    val _currentSelectID: StateFlow<Int> = currentSelectID

    private val modifySuccess: MutableSharedFlow<Unit> = MutableSharedFlow()
    val _modifySuccess: SharedFlow<Unit> = modifySuccess

    init {
        getUserAccountDetailFromRemote()
    }

    fun getUserAccountDetailFromRemote() {
        launchUIWithDialog {
            securityMessage.value = mainRepository.getUserAccountSecurityMessage()
        }
    }

    fun updateSecurityId(id: Int) {
        currentSelectID.value = id
    }

    fun updateSecurityQuestion(answer: String) {
        launchUIWithDialog {
            val type =  if (_securityMessage.value.securityQuestionId == -1) 1 else 0
            mainRepository.updateUserAccountSecurity(
                questionId = _currentSelectID.value,
                answer = answer,
                type = type
            )
            modifySuccess.emit(Unit)
        }
    }
}
