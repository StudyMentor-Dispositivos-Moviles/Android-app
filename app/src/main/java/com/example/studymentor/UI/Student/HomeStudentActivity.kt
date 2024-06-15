package com.example.studymentor.UI.Student

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studymentor.R
import com.example.studymentor.StudentCalendarActivity

class HomeStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btPerfil = findViewById<ImageButton>(R.id.btPerfilEstudiante)
        val btTutor = findViewById<ImageButton>(R.id.btTutors)
        val btCalendar = findViewById<ImageButton>(R.id.btCalendar)


        btTutor.setOnClickListener {
            val intent = Intent(this@HomeStudentActivity, TutorListActivity::class.java)
            startActivity(intent)
        }

        btPerfil.setOnClickListener{
            val intent = Intent(this@HomeStudentActivity, StudentProfileActivity::class.java)
            startActivity(intent)
        }

        btCalendar.setOnClickListener{
            val intent = Intent(this@HomeStudentActivity, StudentCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}