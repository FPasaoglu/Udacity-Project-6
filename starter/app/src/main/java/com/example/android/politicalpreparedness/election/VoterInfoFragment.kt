package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.ElectionRepository

class VoterInfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val database = ElectionDatabase.getInstance(requireContext())
        val repository = ElectionRepository(database)
        val viewModel: VoterInfoViewModel by viewModels(factoryProducer = { VoterInfoViewModelFactory(repository) })

        val args: VoterInfoFragmentArgs by navArgs()
        val electionId = args.argElectionId
        val division = args.argDivision

        val binding =
                DataBindingUtil.inflate<FragmentVoterInfoBinding>(inflater, R.layout.fragment_voter_info, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getVoterInfo(division, electionId)
        viewModel.getElectionFromRepo(electionId)

        viewModel.votingLocationFinderUrl.observe(viewLifecycleOwner, Observer { url ->
            url?.let {
                loadUrlIntent(it)
            }
        })
        viewModel.electionInfoUrl.observe(viewLifecycleOwner, Observer {url ->
            url?.let {
                loadUrlIntent(it)
            }
        })
        viewModel.ballotInfoUrl.observe(viewLifecycleOwner, Observer {url ->
            url?.let {
                loadUrlIntent(it)
            }
        })
        viewModel.savedElection.observe(viewLifecycleOwner, Observer { election ->
            if (election == null) {
                binding.followButton.text = getString(R.string.follow_election)
            } else {
                binding.followButton.text = getString(R.string.unfollow_election)
            }
        })
        viewModel.showToastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        })

        return binding.root
    }

    fun loadUrlIntent(urlString: String) {
        val uri = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}