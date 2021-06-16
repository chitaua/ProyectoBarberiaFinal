package es.manuelgonzalez.proyectobarberia.ui.historyDates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.manuelgonzalez.proyectobarberia.data.model.Date
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.databinding.HistoryDateItemBinding

class HistoryDatesFragmentAdapter() :
    ListAdapter<Date, HistoryDatesFragmentAdapter.ViewHolder>(DateDiffCallback) {

    init {
        setHasStableIds(true)
    }

    lateinit var user: User
    var changeButton = false

    var onDoReviewClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = HistoryDateItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(currentList[position])

    fun sendData(user: User) {
        this.user = user
    }

    inner class ViewHolder(private val binding: HistoryDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            date.run {
                binding.lblDayDate.text = dayDate
                binding.lblHourDate.text = hourDate
                if (user.admin) {
                    binding.btnDoReview.text = "VER USUARIO"
                }
            }

        }

        init {
            binding.btnDoReview.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDoReviewClickListener?.invoke(position)
                }
            }
        }
    }

    object DateDiffCallback : DiffUtil.ItemCallback<Date>() {
        override fun areItemsTheSame(oldItem: Date, newItem: Date): Boolean =
            oldItem.idDate == newItem.idDate


        override fun areContentsTheSame(oldItem: Date, newItem: Date): Boolean =
            oldItem == newItem

    }
}