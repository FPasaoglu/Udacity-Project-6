package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.ResponseState
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase) {

    val savedElections: LiveData<List<Election>> = database.electionDao.getElections()

    val _election = MutableLiveData<Election>()
    val election : LiveData<Election>
        get() = _election

    suspend fun getElections() : ResponseState<List<Election>> = withContext(Dispatchers.IO) {

        try {
            val electionResponse = CivicsApi.retrofitService.getElections()
            return@withContext ResponseState.Success(electionResponse.elections)
        } catch (e: Exception) {
            return@withContext ResponseState.Error(e.localizedMessage)
        }
    }

    suspend fun getVoterInfo(address: String, electionId: Int) = withContext(Dispatchers.IO) {
        try {
            val voterResponse = CivicsApi.retrofitService.getVoterInfo(address, electionId)
            return@withContext ResponseState.Success(voterResponse)
        }catch (e: Exception) {
            return@withContext ResponseState.Error(e.localizedMessage)
        }
    }

    suspend fun getRepresentative(address: String) = withContext(Dispatchers.IO) {
        try {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address)
            val representatives = offices.flatMap { office -> office.getRepresentatives(officials) }
            return@withContext ResponseState.Success(representatives)
        }catch (e: Exception) {
            return@withContext ResponseState.Error(e.localizedMessage)
        }
    }

    suspend fun getElectionById(electionId: Int): Election = withContext(Dispatchers.IO) {
            return@withContext database.electionDao.getElectionById(electionId)
        }

    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
    }

    suspend fun deleteElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.deleteElection(election)
        }
    }
}