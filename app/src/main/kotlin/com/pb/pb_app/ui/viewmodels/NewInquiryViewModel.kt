package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.models.projects.NewInquiry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NewInquiryViewModel(context: Context) : ViewModel() {
    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return NewInquiryViewModel(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    private val repository = RepositoryImpl(context)

    private val newInquiry = MutableStateFlow(NewInquiry("", "", System.currentTimeMillis(), 0, "", "", "", false))

    val shouldEnableSaveButton = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            newInquiry.collect {
                shouldEnableSaveButton.emit(it.name.isNotEmpty() && it.description.isNotEmpty() && it.deadlineMillis != 0L && it.service.isNotEmpty() && it.contactNumber.isNotEmpty() && it.deliveryArea.isNotEmpty())
            }
        }
    }

    fun createNewInquiry() {
        viewModelScope.launch {
            repository.createNewInquiry(newInquiry.value)
        }
    }

    fun updateNewInquiry(
        name: String = newInquiry.value.name,
        description: String = newInquiry.value.description,
        deadlineMillis: Long = newInquiry.value.deadlineMillis,
        service: String = newInquiry.value.service,
        contactNumber: String = newInquiry.value.contactNumber,
        deliveryArea: String = newInquiry.value.deliveryArea,
        reference: Boolean = newInquiry.value.reference,
    ) {
        viewModelScope.launch {
            newInquiry.emit(NewInquiry(name, description, System.currentTimeMillis(), deadlineMillis,service, contactNumber, deliveryArea, reference))
        }
    }

}