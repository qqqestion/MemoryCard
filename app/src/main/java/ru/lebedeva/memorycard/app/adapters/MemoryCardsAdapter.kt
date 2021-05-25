package ru.lebedeva.memorycard.app.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lebedeva.memorycard.databinding.ItemMemoryCardRvBinding
import ru.lebedeva.memorycard.domain.MemoryCard
import java.util.*

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

    private var onItemClickListener: ((MemoryCard) -> Unit)? = null

    fun setOnItemClickListener(i: (MemoryCard) -> Unit) {
        onItemClickListener = i
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memoryCard = cards[position]
        onItemClickListener?.let {
            holder.setOnItemClickListener(it)
        }
        holder.binding.clItem.setOnClickListener {
            onItemClickListener?.let { click ->
                click(memoryCard)
            }
        }
        holder.bind(memoryCard)

    }

    override fun getItemCount(): Int {
        return cards.size
    }


    class ViewHolder(
        val binding: ItemMemoryCardRvBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var _onItemClickListener: ((MemoryCard) -> Unit)? = null

        fun setOnItemClickListener(i: ((MemoryCard) -> Unit)) {
            _onItemClickListener = i
        }

        fun bind(card: MemoryCard) {
            with(binding) {
                tvCardTitle.text = card.title
                val calendar = Calendar.getInstance()
                calendar.time = card.date!!.toDate()
                setDateTimeInTextView(calendar, tvCardDate, this@ViewHolder.itemView.context)
//                clItem.setOnClickListener {
//                    _onItemClickListener?.let { click ->
//                        click(card)
//                    }
//                }
            }
        }

        private fun setDateTimeInTextView(
            pickedDateTime: Calendar,
            view: TextView,
            context: Context
        ) {
            view.text = DateUtils.formatDateTime(
                context,
                pickedDateTime.timeInMillis,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                        or DateUtils.FORMAT_SHOW_TIME
            )
        }
    }
}