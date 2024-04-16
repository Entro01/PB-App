package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import com.pb.pb_app.data.models.inquiries.NewInquiry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _newInquiry = MutableStateFlow(NewInquiry("", "", System.currentTimeMillis(), 0, "", "", "", false))
    val newInquiry get() = _newInquiry as StateFlow<NewInquiry>

    val shouldEnableSaveButton = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            _newInquiry.collect {
                shouldEnableSaveButton.emit(it.name.isNotEmpty() && it.description.isNotEmpty() && it.deadlineMillis > 0L && it.service.isNotEmpty() && it.contactNumber.isNotEmpty() && it.deliveryArea.isNotEmpty())
            }
        }
    }

    fun createNewInquiry() {
        viewModelScope.launch {
            repository.updateInquiryStatus(InquiryUpdateAction.CreateInquiryAsAdmin(_newInquiry.value))
        }
    }

    fun updateNewInquiry(
        name: String = _newInquiry.value.name,
        description: String = _newInquiry.value.description,
        deadlineMillis: Long = _newInquiry.value.deadlineMillis,
        service: String = _newInquiry.value.service,
        contactNumber: String = _newInquiry.value.contactNumber,
        deliveryArea: String = _newInquiry.value.deliveryArea,
        reference: Boolean = _newInquiry.value.reference,
    ) {
        viewModelScope.launch {
            _newInquiry.emit(NewInquiry(name, description, System.currentTimeMillis(), deadlineMillis, service, contactNumber, deliveryArea, reference))
        }
    }

}