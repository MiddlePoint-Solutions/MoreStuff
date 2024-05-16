package io.middlepoint.morestuff.android.app.presentation.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.toLiveData() = this as LiveData<T>
