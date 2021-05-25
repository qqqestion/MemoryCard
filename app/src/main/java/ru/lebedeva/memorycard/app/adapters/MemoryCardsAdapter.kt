package ru.lebedeva.memorycard.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lebedeva.memorycard.databinding.ItemMemoryCardRvBinding
import ru.lebedeva.memorycard.domain.MemoryCard

class MemoryCardsAdapter : RecyclerView.Adapter<MemoryCardsAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<MemoryCard>() {
        override fun areItemsTheSame(oldItem: MemoryCard, newItem: MemoryCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MemoryCard, newItem: MemoryCard): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, callback)

    var cards: List<MemoryCard>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMemoryCardRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memoryCard = cards[position]
        holder.bind(memoryCard)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    class ViewHolder(
        private val binding: ItemMemoryCardRvBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: MemoryCard) {
            with(binding) {
                tvCardTitle.text = card.title
                tvCardDate.text = card.date.toString()
            }
        }
    }
}