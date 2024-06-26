package com.example.studymentor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.example.studymentor.UI.PaymentActivity
import com.example.studymentor.UI.Student.HomeStudentActivity
import com.example.studymentor.UI.Student.StudentProfileActivity
import com.example.studymentor.UI.Student.TutorListActivity
import com.example.studymentor.apiservice.RetrofitClient
import com.example.studymentor.model.Schedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentCalendarActivity : AppCompatActivity() {

    private lateinit var  monthSpinner: Spinner
    private lateinit var calendarGrid: GridLayout
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private  val scheduledClasses = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        monthSpinner = findViewById(R.id.spMonth)
        calendarGrid = findViewById(R.id.calendar_grid)

        setupMonthSpinner()

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("StudentId", 1)
        fetchSchedulesFromApi(userId)

        val btHome = findViewById<ImageButton>(R.id.btHome)
        val btPerfil = findViewById<ImageButton>(R.id.btPerfilEstudiante)
        val btTutor = findViewById<ImageButton>(R.id.btTutors)

        btHome.setOnClickListener {
            val intent = Intent(this@StudentCalendarActivity, HomeStudentActivity::class.java)
            startActivity(intent)
        }


        btPerfil.setOnClickListener {
            val intent = Intent(this@StudentCalendarActivity, StudentProfileActivity::class.java)
            startActivity(intent)
        }

        btTutor.setOnClickListener {
            val intent = Intent(this@StudentCalendarActivity, TutorListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchSchedulesFromApi(studentId: Int) {
        RetrofitClient.scheduleService.getSchedulesByStudent(studentId).enqueue(object :
            Callback<List<Schedule>> {
            override fun onResponse(call: Call<List<Schedule>>, response: Response<List<Schedule>>) {
                if (response.isSuccessful) {
                    val schedules = response.body()
                    schedules?.let {
                        for (schedule in it) {
                            if (!schedule.isAvailable) {
                                val day = Calendar.getInstance().apply {
                                    time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(schedule.day)
                                }.get(Calendar.DAY_OF_MONTH)
                                scheduledClasses[day] = schedule.startingHour
                            }
                        }
                        setupCalendar()
                    }
                }
            }

            override fun onFailure(call: Call<List<Schedule>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setupMonthSpinner(){
        val months = arrayListOf<String>()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        for (i in -3..3){
            calendar.set(currentYear, currentMonth + i, 1)
            months.add(dateFormat.format(calendar.time))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter
        monthSpinner.setSelection(3)

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                calendar.set(currentYear, currentMonth + position - 3, 1)
                setupCalendar()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupCalendar() {
        calendarGrid.removeAllViews()

        val daysOfWeek = arrayOf("L", "M", "X", "J", "V", "S", "D")
        for (day in daysOfWeek) {
            val textView = TextView(this).apply {
                text = day
                textSize = 16f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.cell_border)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(48)
                    height = dpToPx(80)
                }
            }
            calendarGrid.addView(textView)
        }

        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
        val offset = if (firstDayOfWeek < 0) 6 else firstDayOfWeek

        for (i in 0 until offset) {
            val emptyView = TextView(this).apply {
                text = ""
                setBackgroundResource(R.drawable.cell_border)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(48)
                    height = dpToPx(70)
                }
            }
            calendarGrid.addView(emptyView)
        }

        for (day in 1..maxDays) {
            val dayTextView = TextView(this).apply {
                text = day.toString()
                textSize = 14f
                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(48)
                    height = dpToPx(70)
                    setBackgroundColor(Color.parseColor("#D8E8E2"))
                }
                scheduledClasses[day]?.let {
                    setBackgroundColor(Color.parseColor("#82A89A")) // Cambia el color de fondo para los días con clases
                    text = "$day\n$it" // Muestra la hora de la clase
                    textSize = 14f
                }
            }
            calendarGrid.addView(dayTextView)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

}