package es.manuelgonzalez.proyectobarberia.ui.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.manuelgonzalez.proyectobarberia.data.model.Review
import es.manuelgonzalez.proyectobarberia.databinding.ReviewItemBinding

class ReviewsFragmentAdapter : ListAdapter<Review, ReviewsFragmentAdapter.ViewHolder>(
    ReviewsFragmentAdapter.ReviewDiffCallback
) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ReviewItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(currentList[position])

    inner class ViewHolder(private val binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            review.run {
                binding.lblDate.text = idDate.substringBefore(" ")
                binding.lblReviewText.text = reviewText
                binding.lblUserName.text = review.
                binding.ratingBar.rating = rating
            }
        }
    }

    object ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem.idDate == newItem.idDate


        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem == newItem

    }
}