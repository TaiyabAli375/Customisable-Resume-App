package com.myexample.customizabletextresumegenerator

import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Response
import yuku.ambilwarna.AmbilWarnaDialog
import android.Manifest


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var retService : ResumeService
    private lateinit var tvLocation: TextView
    private lateinit var resumeTv : TextView
    private lateinit var cardView : CardView
    private lateinit var tvFontSizeLabel : TextView
    private lateinit var fontSizeSlider : SeekBar
    private lateinit var btnFontColor : CardView
    private lateinit var btnBackgroundColor : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLocation = findViewById<TextView>(R.id.tvLocation)
        resumeTv = findViewById<TextView>(R.id.tvResume)
        btnFontColor = findViewById<CardView>(R.id.btnFontColor)
        btnBackgroundColor = findViewById<CardView>(R.id.btnBackgroundColor)
        cardView = findViewById<CardView>(R.id.cardView)
        fontSizeSlider = findViewById<SeekBar>(R.id.fontSizeSlider)
        tvFontSizeLabel = findViewById<TextView>(R.id.tvFontSizeLabel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        } else {
            getLocation()
        }

        displayResume()
        changeFontSize()
        changeFontColor()
        changeBackgroundColor()
    }

    private fun displayResume(){
        retService = RetrofitInstance.getRetrofitInstance().create(ResumeService::class.java)
        val pathResponse: LiveData<Response<ResumeData>> = liveData {
            val response = retService.getResumeData("insert-your-name-here")
            emit(response)
        }
        pathResponse.observe(this, Observer {
            val resumeData = it.body()
            val skills = resumeData?.skills
            val projects = resumeData?.projects
            var mySkill = ""
            var myProjects = ""
            var projectsListString = ""
            if (resumeData != null) {
                if (skills != null) {
                    for (skill in skills) {
                        val skill = skill
                        mySkill += skill + ", "
                    }
                }

                if (projects != null) {
                    for (project in projects) {
                        val project = project
                        myProjects = "\u2022 ${project.title}: ${project.description}\n" +
                                "(${project.startDate}) to " +
                                "(${project.endDate})\n\n"
                        projectsListString += myProjects
                    }
                }
                resumeTv.text = "Name: ${resumeData.name}\nEmail: ${resumeData.email}\n" +
                        "Twitter: ${resumeData.twitter}\nPhone: ${resumeData.phone}\n" +
                        "Address: ${resumeData.address}\n\n" +
                        "SUMMARY\n-----------------------\n${resumeData.summary}\n\n" +
                        "SKILLS\n-----------------------\n${mySkill}\n\nPROJECTS\n" +
                        "-----------------------\n ${projectsListString}"
            } else{
                Toast.makeText(this, "Empty response received.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changeBackgroundColor(){
        btnBackgroundColor.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, Color.WHITE,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {}
                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        cardView.setCardBackgroundColor(color)
                    }
                })
            dialog.show()
        }
    }

    private fun changeFontColor(){
        btnFontColor.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, Color.BLACK,
                object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {}
                    override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                        resumeTv.setTextColor(color)
                    }
                })
            dialog.show()
        }
    }

    private fun changeFontSize(){
        tvFontSizeLabel.text = "Font Size: ${fontSizeSlider.progress}"
        val initialSize = fontSizeSlider.progress + 10
        resumeTv.textSize = initialSize.toFloat()
        tvFontSizeLabel.text = "Font Size: ${fontSizeSlider.progress}"

        fontSizeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = progress + 10 // prevent too small
                resumeTv.textSize = size.toFloat()
                tvFontSizeLabel.text = "Font Size: $size"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
            fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val lat = it.latitude
                    val lon = it.longitude
                    tvLocation.text = "Lat: $lat, Long: $lon"
                } ?: run {
                    tvLocation.text = "Location unavailable"
                }
            }
    }
}