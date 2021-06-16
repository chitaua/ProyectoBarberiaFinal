package es.manuelgonzalez.proyectobarberia.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import es.manuelgonzalez.proyectobarberia.data.model.Photo
import es.manuelgonzalez.proyectobarberia.databinding.PhotoListItemBinding


class GalleryFragmentAdapter : RecyclerView.Adapter<GalleryFragmentAdapter.ViewHolder>() {

    private var data: List<Photo> = emptyList()


    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryFragmentAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PhotoListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun submitList(newData: List<Photo>) {
        data = newData
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: PhotoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.run {
                imgPhoto.load(photo.url)
            }
        }

    }
}