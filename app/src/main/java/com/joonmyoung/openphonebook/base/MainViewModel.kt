package com.joonmyoung.openphonebook.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _certifyPhoneNum = MutableLiveData<Boolean>()
    val currentCertify: LiveData<Boolean> = _certifyPhoneNum
    private var certifyPhoneNum = false

    private val _checkOverlapEmail = MutableLiveData<Boolean>()
    val currentCheckOverlapEmail: LiveData<Boolean> = _checkOverlapEmail
    private var checkOverlapEmail = false

    private val _userPhoneNum = MutableLiveData<String>()
    val currentPhoneNum: LiveData<String> = _userPhoneNum
    private var userPhoneNum = ""

    init {
        _certifyPhoneNum.value = certifyPhoneNum
        _checkOverlapEmail.value = checkOverlapEmail
        _userPhoneNum.value = userPhoneNum
    }

    fun getCertify(): Boolean {
        return certifyPhoneNum
    }

    fun setCertify(bool: Boolean) {
        certifyPhoneNum = bool
        _certifyPhoneNum.value = certifyPhoneNum
    }

    fun getOverlap(): Boolean {
        return checkOverlapEmail
    }

    fun setOverlap(bool: Boolean) {
        checkOverlapEmail = bool
        _checkOverlapEmail.value = checkOverlapEmail
    }

    fun setPhoneNum(text: String) {
        userPhoneNum = text
        _userPhoneNum.value = userPhoneNum
    }

    fun getPhoneNum(): String {
        return userPhoneNum
    }
}