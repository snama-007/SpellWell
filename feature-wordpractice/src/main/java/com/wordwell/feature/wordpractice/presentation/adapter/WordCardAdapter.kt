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
    private val onFavoriteClick: (Word) -> Unit
) : ListAdapter<Word, WordCardAdapter.WordCardViewHolder>(WordCardDiffCallback()) {

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

    inner class WordCardViewHolder(
        private val binding: ItemWordCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(word: Word) {
            binding.apply {
                wordTextView.text = word.word
                phoneticsTextView.text = "/${word.phonetics[0].text}/"
                meaningTextView.text = word.definitions[0].meaning

                playButton.setOnClickListener {
                    onPlayClick(word)
                }

                favoriteButton.setOnClickListener {
                    onFavoriteClick(word)
                }
            }
        }
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