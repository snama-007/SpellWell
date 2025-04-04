package com.wordwell.feature.wordpractice.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wordwell.feature.wordpractice.databinding.FragmentWordPracticeBinding
import com.wordwell.feature.wordpractice.presentation.adapter.WordSetAdapter
import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.utils.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WordPracticeFragment : Fragment() {

    private var _binding: FragmentWordPracticeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var wordWellServer: WordWellServer

    private val viewModel: WordPracticeViewModel by viewModels {
        WordPracticeViewModel.Factory(wordWellServer.cachedWordsViewModelFactory)
    }

    private lateinit var wordSetAdapter: WordSetAdapter
    private var currentSetName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupToolbar()
    }

    private fun setupRecyclerView() {
        wordSetAdapter = WordSetAdapter { wordSet ->
            LogUtils.log("Selected set: ${wordSet.name}")
            currentSetName = wordSet.name
            viewModel.loadWordsForSet(wordSet.name)
        }

        binding.wordSetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wordSetAdapter
        }
    }

    private fun setupObservers() {
        viewModel.wordSets.observe(viewLifecycleOwner) { sets ->
            LogUtils.log("Received word sets: ${sets.wordSet}")
            wordSetAdapter.submitList(sets.wordSet)
        }

        viewModel.currentWords.observe(viewLifecycleOwner) { wordResult ->
            wordResult?.let {
                findNavController().navigate(
                    WordPracticeFragmentDirections.actionWordPracticeFragmentToWordCardFragment(
                        setName = currentSetName,
                        wordResult = it
                    )
                )
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 