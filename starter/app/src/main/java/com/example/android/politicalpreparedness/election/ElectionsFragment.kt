package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.util.gone
import com.example.android.politicalpreparedness.util.visible
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val database = ElectionDatabase.getInstance(requireContext())
        val repository = ElectionRepository(database)
        val viewModel by viewModels<ElectionsViewModel>(factoryProducer = { ElectionsViewModelFactory(repository) })
        var binding: FragmentElectionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        // ⚡️ upcomingElection
        val upcomingElectionListAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToVoterInfoAbout(election)
        })
        binding.upcomingElectionRecylerView.adapter = upcomingElectionListAdapter
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { electionList ->
            electionList?.let {
                upcomingElectionListAdapter.submitList(electionList)
            }
        })

        val savedElectionListAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.navigateToVoterInfoAbout(election)
        })
        binding.savedElectionRecylerView.adapter = savedElectionListAdapter
        viewModel.savedElections.observe(viewLifecycleOwner, Observer { electionList ->
            electionList?.let {
                savedElectionListAdapter.submitList(electionList)
            }
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
                viewModel.navigationToVoterInfoDone()
            }
        })


        viewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoad ->
            if (isLoad) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visible()
                binding.savedElectionRecylerView.gone()
                binding.upcomingElectionRecylerView.gone()
                binding.savedElectionsHeaderText.gone()
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.gone()
                binding.savedElectionRecylerView.visible()
                binding.upcomingElectionRecylerView.visible()
                binding.savedElectionsHeaderText.visible()
            }
        })

        viewModel.showSnackbar.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
            }

        })

        return binding.root
    }

}