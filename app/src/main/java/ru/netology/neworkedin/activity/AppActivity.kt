package ru.netology.neworkedin.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkedin.R
import ru.netology.neworkedin.utils.FloatValue.currentFragment
import ru.netology.neworkedin.utils.FloatValue.textNewPost
import ru.netology.neworkedin.viewmodel.AuthViewModel

/** ЗАПУСК ПРИЛОЖЕНИЕ, ГУГЛ */

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    //создание
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        checkGoogleApiAvailability()
    }

    //работаем с гуглом
    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)

            if (code == ConnectionResult.SUCCESS) {
                return@with
            }

            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }

            Toast.makeText(this@AppActivity, "Google Api Unavailable", Toast.LENGTH_LONG).show()
        }
    }

    //нажата кнопка "назад"
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentFragment == "NewPostFragment") {
            findViewById<FloatingActionButton>(R.id.fab_cancel).callOnClick()
            return
        }

        super.onBackPressed()
    }

    //приложение перестаёт работать
    override fun onStop() {
        currentFragment = ""
        textNewPost = ""
        super.onStop()
    }
}