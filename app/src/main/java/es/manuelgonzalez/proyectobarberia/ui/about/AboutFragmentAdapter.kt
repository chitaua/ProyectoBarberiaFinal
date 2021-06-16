package es.manuelgonzalez.proyectobarberia.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.manuelgonzalez.proyectobarberia.data.model.Schedule
import es.manuelgonzalez.proyectobarberia.databinding.ScheduleItemBinding

class AboutFragmentAdapter : RecyclerView.Adapter<AboutFragmentAdapter.ViewHolder>() {

    private var data: List<Schedule> = listOf(
        Schedule("Lunes", "9:00 - 13:30", "16:00 - 20:30"),
        Schedule("Martes", "9:00 - 13:30", "16:00 - 20:30"),
        Schedule("Miércoles", "9:00 - 13:30", "16:00 - 20:30"),
        Schedule("Jueves", "9:00 - 13:30", "16:00 - 20:30"),
        Schedule("Viernes", "9:00 - 13:30", "16:00 - 20:30"),
        Schedule("Sábado", "Cerrado", ""),
        Schedule("Domingo", "Cerrado", ""),
    )

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ScheduleItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(private val binding: ScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.lblDayOfWeek.text = schedule.dayOfWeek
            binding.lblMorningSchedule.text = schedule.morningSchedule
            binding.lblAfternoonSchedule.text = schedule.afternoonSchedule
        }
    }
}