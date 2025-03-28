package com.wordwell.feature.wordpractice.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wordwell.feature.wordpractice.R
import com.wordwell.feature.wordpractice.databinding.FragmentWordCardBinding
import com.wordwell.feature.wordpractice.presentation.adapter.WordCardAdapter
import com.wordwell.libwwmw.utils.LogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordCardFragment : Fragment() {

    private var _binding: FragmentWordCardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: WordCardViewModel by viewModels()
    private lateinit var wordCardAdapter: WordCardAdapter
    private val args: WordCardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupSwipeGestures()
        setupObservers()
        setupToolbar()
    }

    private fun setupViewModel() {
        LogUtils.log(args.wordResult.toString())
        viewModel.initializeWithWordResult(args.wordResult)
    }

    private fun setupRecyclerView() {
        wordCardAdapter = WordCardAdapter(
            onPlayClick = { word ->
                LogUtils.log("Play pronunciation for: ${word.word}")
                viewModel.playPronunciation(word)
            },
            onFavoriteClick = { word ->
                LogUtils.log("Toggle favorite for: ${word.word}")
                viewModel.toggleFavorite(word)
            },
            onNextClick = { position ->
                if (!viewModel.moveToNextWord()) {
                    showRubberBandEffect()
                }
            },
            onPreviousClick = { position ->
                if (!viewModel.moveToPreviousWord()) {
                    showRubberBandEffect()
                }
            }
        )

        binding.wordCardRecyclerView.apply {
            adapter = wordCardAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeGestures() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        LogUtils.log("Swipe left - next word")
                        viewModel.moveToNextWord()
                    }
                    ItemTouchHelper.RIGHT -> {
                        LogUtils.log("Swipe right - previous word")
                        viewModel.moveToPreviousWord()
                    }
                    ItemTouchHelper.DOWN -> {
                        val word = wordCardAdapter.getCurrentWord(viewHolder.adapterPosition)
                        LogUtils.log("Swipe down - add to favorites: ${word.word}")
                        viewModel.toggleFavorite(word)
                        Snackbar.make(
                            binding.root,
                            "Added to favorites",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.wordCardRecyclerView)
    }

    private fun setupObservers() {
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            word?.let {
                wordCardAdapter.submitList(listOf(it))
            }
        }

        viewModel.totalCards.observe(viewLifecycleOwner) { total ->
            wordCardAdapter.setTotalCount(total)
        }

        viewModel.currentCardNumber.observe(viewLifecycleOwner) { current ->
            wordCardAdapter.setCurrentPosition(current)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title = args.setName.uppercase()
            setNavigationIcon(R.drawable.ic_close)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun showRubberBandEffect() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rubber_band)
        binding.wordCardRecyclerView.startAnimation(animation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 