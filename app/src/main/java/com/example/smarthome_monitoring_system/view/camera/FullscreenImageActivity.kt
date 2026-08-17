package com.example.smarthome_monitoring_system.view.camera

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R

class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var fullscreenImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_fullscreen_image
        )

        fullscreenImage =
            findViewById(
                R.id.imageFullscreen
            )

        val imageResId =
            intent.getIntExtra(
                EXTRA_IMAGE_RES_ID,
                R.drawable.mock_camera_snapshot
            )

        fullscreenImage.setImageResource(
            imageResId
        )

        setupBackButton()
    }

    private fun setupBackButton() {

        val backButton =
            findViewById<ImageButton>(
                R.id.buttonFullscreenBack
            )

        backButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        const val EXTRA_IMAGE_RES_ID =
            "image_res_id"
    }
}