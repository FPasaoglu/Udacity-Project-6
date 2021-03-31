package com.example.android.politicalpreparedness.election

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.ResponseState
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val repository: ElectionRepository) : ViewModel() {


    val voterAdministrationBody = MutableLiveData<AdministrationBody>()
    val showToastMessage = MutableLiveData<String>()

    val electionInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl = MutableLiveData<String>()
    val votingLocationFinderUrl = MutableLiveData<String>()

    val election = MutableLiveData<Election>()
    val savedElection = MutableLiveData<Election>()

    fun getVoterInfo(division: Division, electionId: Int) {
        val address = "${division.state} ${division.country}"
        viewModelScope.launch {
            val voterResponse = repository.getVoterInfo(address, electionId)
            when (voterResponse) {
                is ResponseState.Success -> {
                    election.value = voterResponse.data.election
                    voterAdministrationBody.value = voterResponse.data.state?.get(0)?.electionAdministrationBody
                }
                is ResponseState.Error -> {
                    showToastMessage.value = voterResponse.message
                }
            }
        }
    }

    fun getElectionFromRepo(electionId: Int) {
        viewModelScope.launch {
            savedElection.value = repository.getElectionById(electionId)
        }
    }


    fun setElectionInfoUrl() {
        electionInfoUrl.value = voterAdministrationBody.value?.electionInfoUrl
    }

    fun setBallotInfoUrl() {
        ballotInfoUrl.value = voterAdministrationBody.value?.ballotInfoUrl
    }

    fun setVotingLocationFinderUrl() {
        votingLocationFinderUrl.value = voterAdministrationBody.value?.votingLocationFinderUrl
    }

    fun followButton() {
        viewModelScope.launch {
            if (savedElection.value == null) {
                election.value?.let { electionValue ->
                    repository.saveElection(electionValue)
                    savedElection.value = electionValue
                }
            } else {
                election.value?.let { electionValue ->
                    savedElection.value = null
                    repository.deleteElection(electionValue)
                }
            }
        }
    }
}