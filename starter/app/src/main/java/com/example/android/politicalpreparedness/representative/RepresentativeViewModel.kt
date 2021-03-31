package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.ResponseState
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val repository: ElectionRepository) : ViewModel() {

    var address = MutableLiveData<Address>()
    val representativeList = MutableLiveData<List<Representative>>()
    val showToastMessage = MutableLiveData<String>()

    init {
        address.value = Address("","","","","")
    }

    fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            val response = repository.getRepresentative(address.toFormattedString())
            when (response) {
                is ResponseState.Success -> {
                    representativeList.value = response.data
                }
                is ResponseState.Error -> {
                    showToastMessage.value = response.message
                }
            }
        }
    }

    fun findRepresentatives() {
        address.value?.let { addressValue ->
            getRepresentatives(addressValue)
        }
    }

    fun useLocation(adressValue: Address) {
        address.value = adressValue
        findRepresentatives()

    }

}
