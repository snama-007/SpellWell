package com.wordwell.feature.wordpractice.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordwell.feature.wordpractice.databinding.ItemWordCardBinding
import com.wordwell.libwwmw.domain.models.Word

class WordCardAdapter(
    private val onPlayClick: (Word) -> Unit,
    private val onFavoriteClick: (Word) -> Unit,
    private val onNextClick: (Int) -> Unit,
    private val onPreviousClick: (Int) -> Unit
) : ListAdapter<Word, WordCardAdapter.WordCardViewHolder>(WordCardDiffCallback()) {

    private var totalCount: Int = 0
    private var currentPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardViewHolder {
        val binding = ItemWordCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getCurrentWord(position: Int): Word = getItem(position)

    fun setTotalCount(count: Int) {
        totalCount = count
        notifyDataSetChanged()

    }

    fun setCurrentPosition(position: Int) {
        currentPosition = position
        notifyDataSetChanged()
    }

    inner class WordCardViewHolder(
        private val binding: ItemWordCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            val screenWidth = binding.root.resources.displayMetrics.widthPixels
            val cardWidth = (screenWidth * 0.9).toInt()
            val horizontalMargin = 10

            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                width = cardWidth
                marginStart = horizontalMargin
                marginEnd = horizontalMargin
            }

            binding.playButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlayClick(getItem(position))
                }
            }

            binding.favoriteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(getItem(position))
                }
            }

            binding.nextButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onNextClick(position)
                }
            }

            binding.previousButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPreviousClick(position)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(word: Word) {
            binding.apply {
                wordTextView.text = word.word
                phoneticsTextView.text = "/${word.phonetics[0].text}/"
                definitionTextView.text = extractMeanings(word.definitions[0].meaning).toString()
                cardCountTextView.text = "${currentPosition} / $totalCount"
            }
        }
    }

    fun extractMeanings(jsonString: String): List<String> {
        val regex = """\{bc\}(.*?)\}""".toRegex()
        val matches = regex.findAll(jsonString)
        val meanings = matches.map { it.groupValues[1].plus("\n") }.toSet()
        return meanings.toList()
    }

    private class WordCardDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
} 