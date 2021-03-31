package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.ResponseState
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class ElectionRepository(private val database: ElectionDatabase) {

    val savedElections: LiveData<List<Election>> = database.electionDao.getElections()

    val election = MutableLiveData<Election>()

    suspend fun getElections() : ResponseState<List<Election>> = withContext(Dispatchers.IO) {

        try {
            val electionResponse = CivicsApi.retrofitService.getElections()
            return@withContext ResponseState.Success(electionResponse.elections)
        } catch (e: Exception) {
            return@withContext ResponseState.Error(e.localizedMessage)
        }
    }

    suspend fun getElectionById(electionId: Int) {
        withContext(Dispatchers.IO) {
            election.postValue(database.electionDao.getElectionById(electionId))
        }
    }


    suspend fun getVoterInfo(address: String, electionId: Int) {
        withContext(Dispatchers.IO) {
            val voterResponse = CivicsApi.retrofitService.getVoterInfo(address, electionId)
            // state.postValue(voterResponse.state?.get(0))
        }
    }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
    }
}