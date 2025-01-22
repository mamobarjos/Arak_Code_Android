package com.arakadds.arak.presentation.activities.ArakStore

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arakadds.arak.R
import com.arakadds.arak.databinding.ActivityShippingAddressBinding
import com.arakadds.arak.databinding.ActivityThankYouBinding
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.utils.AppProperties
import dagger.android.AndroidInjection
import io.paperdb.Paper

class ThankYouActivity : AppCompatActivity() {

    lateinit var binding: ActivityThankYouBinding
    private var language: String = "ar"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityThankYouBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this?.let { Paper.init(it) }
        Paper.book().write("language", AppProperties.SELECTED_LANGUAGE)
        language = Paper.book().read("language")!!


        binding.finishButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }
}