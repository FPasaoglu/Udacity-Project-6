package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.ResponseState
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: ElectionRepository): ViewModel() {

    val upcomingElections = MutableLiveData<List<Election>>()
    val savedElections = repository.savedElections
    val showLoading = MutableLiveData<Boolean>()
    val showSnackbar = MutableLiveData<String?>()

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    init {
        getElections()
    }

    private fun getElections() {
        showLoading.value = true
        viewModelScope.launch {
            val electionState = repository.getElections()
            when(electionState) {
                is ResponseState.Success -> {
                    showLoading.value = false
                    upcomingElections.value = electionState.data
                }

                is ResponseState.Error -> {
                    showLoading.value = false
                    showSnackbar.value = electionState.message
                }
            }

        }
    }

    fun navigateToVoterInfoAbout(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun navigationToVoterInfoDone() {
        _navigateToVoterInfo.value = null
    }

}
