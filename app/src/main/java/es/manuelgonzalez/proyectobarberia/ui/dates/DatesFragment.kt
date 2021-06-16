package es.manuelgonzalez.proyectobarberia.ui.dates

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.rpv.msm.utils.RecyclerAdapter
import es.manuelgonzalez.proyectobarberia.R
import es.manuelgonzalez.proyectobarberia.data.model.Hour
import es.manuelgonzalez.proyectobarberia.data.model.User
import es.manuelgonzalez.proyectobarberia.databinding.CalendarDayBinding
import es.manuelgonzalez.proyectobarberia.databinding.CalendarHeaderBinding
import es.manuelgonzalez.proyectobarberia.databinding.DatesFragmentBinding
import es.manuelgonzalez.proyectobarberia.ui.userInfo.UserInfoFragment
import es.manuelgonzalez.proyectobarberia.utils.*
import es.manuelgonzalez.proyectobarberia.utils.FirebaseUtils.firebaseAuth
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class DatesFragment : Fragment(R.layout.dates_fragment) {


    private val binding: DatesFragmentBinding by viewBinding {
        DatesFragmentBinding.bind(it)
    }

    private val viewModel: DatesFragmentViewModel by viewModels()

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now().plusDays(1)
    private var startDate: LocalDate? = today
    private var endDate: LocalDate? = today.plusMonths(2)

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter =
        DateTimeFormatter.ofPattern("EEEE d MMM yyyy")
    private val selectedFormatter =
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())


    private lateinit var currentUser: User
    private lateinit var citaActiva: String

    private val hours = mutableMapOf<LocalDate, List<Hour>>()

    private val dates = mutableListOf(
        "9:00",
        "9:45",
        "10:30",
        "11:15",
        "12:00",
        "12:45",
        "16:00",
        "16:45",
        "17:30",
        "18:15",
        "19:00",
        "19:45"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        setupViews()

        binding.calendar.apply {
            setup(currentMonth, currentMonth.plusMonths(2), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            binding.calendar.post {
                // Show day initially.
                selectDate(today)
            }
        }

        //CONFIGURACIÓN CALENDARIO
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH &&
                        (day.date == today || day.date.isAfter(today)) &&
                        (day.date.isBefore(today.plusMonths(2))) &&
                        (day.date.dayOfWeek.value != 6) &&
                        (day.date.dayOfWeek.value != 7)
                    ) {
                        val date = day.date
                        if (startDate != null) {
                            if (date < startDate || endDate != null) {
                                startDate = date
                                endDate = null
                            } else if (date != startDate) {
                                endDate = date
                            }
                        } else {
                            startDate = date
                        }


                        this@DatesFragment.binding.calendar.notifyCalendarChanged()
                        selectDate(day.date)
                    }
                }
            }
        }

        binding.calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    textView.makeVisible()

                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.day_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.blue)
                            textView.setBackgroundResource(R.drawable.day_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            dotView.isVisible = hours[day.date].orEmpty().isNotEmpty()
                        }
                    }
                    if (day.date.isBefore(today) || day.date.isAfter(
                            today.plusMonths(2).minusDays(1)
                        ) || day.date.dayOfWeek.value == 6 || day.date.dayOfWeek.value == 7
                    ) {
                        textView.setTextColorRes(R.color.gray)
                        textView.background = null
                    }


                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        binding.calendar.monthScrollListener = {
            // ESTO ES PARA CAMBIAR EL LABEL SUPERIOR DEL MES
            binding.lblSelectedMonthYear.text = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }

            // Select the first day of the month when
            // we scroll to a new month.
            if (it.month == today.monthValue) {
                selectDate(it.yearMonth.atDay(today.dayOfMonth))
            } else {
                var numberDay = 1
                while (it.yearMonth.atDay(numberDay).dayOfWeek == DayOfWeek.SATURDAY ||
                    it.yearMonth.atDay(numberDay).dayOfWeek == DayOfWeek.SUNDAY
                ) {
                    numberDay++
                }
                selectDate(it.yearMonth.atDay(numberDay))
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.calendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth
                        container.legendLayout.tag = month.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                //tv.text = daysOfWeek[index].name.first().toString() EN INGLÉS
                                tv.text = daysOfWeek[index].getDisplayName(
                                    TextStyle.NARROW,
                                    Locale.getDefault()
                                )
                                tv.setTextColorRes(R.color.black)
                            }
                    }
                }
            }

        //checkIfUserHaveActiveDate()
        //FIN CONFIGURACIÓN CALENDARIO
    }


    private fun setupViews() {
        setupToolbar()
        observeUser()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner, { user ->
            currentUser = user
            if (currentUser.activeDate) {
                viewModel.getActiveDateByUser(user).observe(viewLifecycleOwner, {
                    binding.txtActiveUserDate.text = it.idDate
                    citaActiva = it.idDate
                })
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbar.run {
            title = getString(R.string.dates_title)
            setNavigationOnClickListener {
                (requireActivity() as OnNavigateUpListener).onNavigateUp()
            }
        }
    }


    private fun setupRecyclerView(
        list: MutableList<String>,
        day: String,
        listAdmin: List<String> = listOf()
    ) {

        binding.btnCancelActiveDate.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cancelar cita")
                .setMessage("¿Estás seguro de que quieres cancelar la cita actual?")
                .setPositiveButton(R.string.cancel) { _, _ ->
                    Log.i("Cita activa", binding.txtActiveUserDate.text.toString())
                    viewModel.deleteDate(binding.txtActiveUserDate.text.toString())
                    viewModel.changeActiveDate(false)
                    observeDates(day)
                    //observeUser()
                    setupNotActiveDateViews()
                }
                .setNegativeButton(R.string.back, null)
                .show()
        }


        val mAdapter = object : RecyclerAdapter<ViewHolderHour>(
            list,
            R.layout.hour_item_view,
            ViewHolderHour::class.java
        ) {


            override fun onBindViewHolder(holder: ViewHolderHour, position: Int) {

                var item = list[position]
                if (currentUser.admin) {
                    if (item in listAdmin) {
                        holder.btnHour.setTextColor(context!!.getColor(R.color.default_app_blue))
                        holder.btnHour.setOnClickListener {
                            navigateToUserInfo("$day ${list.get(position)}")
                        }
                    } else {
                        holder.btnHour.setOnClickListener {
                            Snackbar.make(
                                binding.root,
                                "Esta cita no está ocupada",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    }

                } else {
                    if (currentUser.activeDate) {
                        setupActiveDateViews()


                    } else {
                        setupNotActiveDateViews()
                        if (item in listAdmin) {
                            holder.btnHour.visibility = View.GONE
                        } else {
                            holder.btnHour.setOnClickListener {
                                AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.select_date_confirmation)
                                    .setMessage(
                                        StringBuilder().append(
                                            binding.lblSelectedDate.text.toString()
                                                .substringAfter("- ").uppercase()
                                        ).append(" - ${list.get(position)}")
                                    )
                                    .setPositiveButton(R.string.confirm) { _, _ ->
                                        viewModel.addDate(
                                            es.manuelgonzalez.proyectobarberia.data.model.Date(
                                                "$day ${list.get(position)}",
                                                firebaseAuth.uid,
                                                day,
                                                list.get(position)
                                            )
                                        )
                                        binding.txtActiveUserDate.text =
                                            "$day ${list.get(position)}"
                                        viewModel.changeActiveDate(true)
                                        observeDates(day)
                                        observeUser()
                                        setupActiveDateViews()
                                    }
                                    .setNegativeButton(R.string.cancel, null)
                                    .show()
                            }
                        }
                    }

                }
                holder.btnHour.text = list[position]
            }
        }


        binding.lstHours.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }


    private fun setupActiveDateViews() {
        binding.run {
            lblActiveUserDate.visibility = View.VISIBLE
            txtActiveUserDate.visibility = View.VISIBLE
            lblActiveDateInfo.visibility = View.VISIBLE
            btnCancelActiveDate.visibility = View.VISIBLE
            calendar.visibility = View.GONE
            lstHours.visibility = View.GONE
            lblSelectedDate.visibility = View.GONE
            lblSelectedMonthYear.visibility = View.GONE
        }

    }

    private fun setupNotActiveDateViews() {
        binding.run {
            calendar.visibility = View.VISIBLE
            lstHours.visibility = View.VISIBLE
            lblSelectedDate.visibility = View.VISIBLE
            lblSelectedMonthYear.visibility = View.VISIBLE
            lblActiveUserDate.visibility = View.GONE
            txtActiveUserDate.visibility = View.GONE
            lblActiveDateInfo.visibility = View.GONE
            btnCancelActiveDate.visibility = View.GONE
        }
    }

    private fun navigateToUserInfo(idDate: String) {
        setFragmentResult("requestKey", bundleOf("bundleKey" to idDate))
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<UserInfoFragment>(R.id.fcContent)
            addToBackStack("")
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendar.notifyDateChanged(it) }
            binding.calendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        observeDates(selectedFormatter.format(date))
        binding.lblSelectedDate.text = "CITAS DISPONIBLES - " + selectionFormatter.format(date)
    }

    private fun observeDates(dia: String) {
        viewModel.queryDatesByDay(dia).observe(viewLifecycleOwner, { listDates ->
            var listaHorasDB = mutableListOf<String>()
            var now = LocalDateTime.now()

            for (it in listDates) {
                listaHorasDB.add(it.hourDate)
            }

            var listaBuena = dates.filter { it !in listaHorasDB }

            //setupRecyclerView(listaBuena, dia)
            setupRecyclerView(dates, dia, listaHorasDB)
        })

    }

}

class ViewHolderHour(val view: View) : RecyclerView.ViewHolder(view) {
    val btnHour = view.findViewById<TextView>(R.id.btnHour)
}