package com.mirza.ali.doctor

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mirza.ali.doctor.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val doctors = mutableListOf<Doctor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(binding) {

            addDoctorButton.setOnClickListener {
                val doctorName = doctorNameEditText.text.toString()
                val consultationTime = consultationTimeEditText.text.toString().toIntOrNull()

                if (doctorName.isNotEmpty() && consultationTime != null) {
                    doctors.add(Doctor(doctorName, consultationTime))
                    shortToast("Doctor added")
                    doctorNameEditText.text.clear()
                    consultationTimeEditText.text.clear()
                } else {
                    shortToast("Please enter valid doctor details")
                }
            }

            calculateButton.setOnClickListener {
                val patientPosition = patientPositionEditText.text.toString().toIntOrNull()

                if (patientPosition != null && patientPosition > 0) {
                    val waitingTime = calculateWaitingTime(doctors, patientPosition)
                    resultTextView.text = "Estimated Waiting Time: $waitingTime minutes"
                    resultTextView.visibility = TextView.VISIBLE
                } else {
                    shortToast("Please enter a valid patient position")
                }
            }

        }

    }

    fun calculateWaitingTime(doctors: List<Doctor>, patientPosition: Int): Int {
        if (doctors.isEmpty() || patientPosition <= 0) {
            return 0
        }

        val totalDoctors = doctors.size
        val patientsPerDoctor = (patientPosition - 1) / totalDoctors  // Subtract 1 to exclude the patient himself
        val remainingPatients = (patientPosition - 1) % totalDoctors  // Subtract 1 to exclude the patient himself

        // Calculate the waiting time for each doctor
        val waitingTimes = doctors.map { doctor ->
            patientsPerDoctor * doctor.avgConsultationTime
        }.toMutableList()

        // Distribute the remaining patients to the doctors with the shortest consultation time
        for (i in 0 until remainingPatients) {
            val minIndex = waitingTimes.indexOf(waitingTimes.minOrNull())
            waitingTimes[minIndex] += doctors[minIndex].avgConsultationTime
        }

        // The patient's waiting time will be the minimum time among all doctors
        return waitingTimes.minOrNull() ?: 0
    }

    fun shortToast(msg: String) {
        val inflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.toast,findViewById(R.id.toast))
        val tvToast: TextView = layout.findViewById(R.id.toast_text)
        tvToast.text = msg

        with(Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            setGravity(Gravity.CENTER, 0, 0) // Center of the screen
            show()
        }


    }

data class Doctor(
    val name: String,
    val avgConsultationTime: Int
)

}