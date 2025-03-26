package com.wordwell.feature.wordpractice.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordwell.feature.wordpractice.databinding.ItemWordSetBinding
import com.wordwell.libwwmw.domain.models.WordSet

class WordSetAdapter(
    private val onSetClick: (WordSet) -> Unit
) : ListAdapter<WordSet, WordSetAdapter.WordSetViewHolder>(WordSetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordSetViewHolder {
        val binding = ItemWordSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordSetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordSetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WordSetViewHolder(
        private val binding: ItemWordSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSetClick(getItem(position))
                }
            }
        }

        fun bind(wordSet: WordSet) {
            binding.apply {
                setNameTextView.text = wordSet.name.uppercase()
                setDescriptionTextView.text = wordSet.name
                wordCountTextView.text = wordSet.numberOfWords.toString()
            }
        }
    }

    private class WordSetDiffCallback : DiffUtil.ItemCallback<WordSet>() {
        override fun areItemsTheSame(oldItem: WordSet, newItem: WordSet): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WordSet, newItem: WordSet): Boolean {
            return oldItem.name == newItem.name
        }
    }
} 