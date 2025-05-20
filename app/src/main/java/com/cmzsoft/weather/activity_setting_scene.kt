package com.cmzsoft.weather


import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_setting_scene : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_scene)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initAnimToggle();
    }

    private fun initAnimToggle() {
//        val toggleContainer = findViewById<FrameLayout>(R.id.toggleContainer)
//        val knob = findViewById<View>(R.id.knob)
//
//        var isOn = true
//        toggleContainer.setOnClickListener {
//            val endX = if (isOn) 0f else (toggleContainer.width - knob.width).toFloat()
//            isOn = !isOn
//            knob.animate()
//                .x(endX)
//                .setDuration(300)
//                .withEndAction {
//                    toggleContainer.setBackgroundResource(if(isOn) R.drawable.bg_toggle_checked else R.drawable.bg_toggle_unchecked);
//                }
//                .start()
//        }
    }

}