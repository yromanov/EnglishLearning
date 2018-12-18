package group.ius.englishlearning

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import group.ius.englishlearning.retrofit.RetrofitBackend.backendService
import group.ius.englishlearning.utils.isValidNick
import group.ius.englishlearning.utils.isValidPassword
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MultipartBody


class LoginActivity : AppCompatActivity() {

    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email_sign_in_button.setOnClickListener { attemptLogin() }

        registerButton.setOnClickListener {
            val infoEditIntent = Intent(this, InfoEditActivity::class.java)
            infoEditIntent.putExtra(InfoEditActivity.IS_REG, true)

            startActivity(infoEditIntent)
        }
    }

    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        nickEditText.error = null
        passwordEditText.error = null

        // Store values at the time of the login attempt.
        val nickStr = nickEditText.text.toString()
        val passwordStr = passwordEditText.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!isValidPassword(passwordStr)) {
            passwordEditText.error = "Invalid password"
            focusView = passwordEditText
            cancel = true
        }

        if (!isValidNick(nickStr)) {
            nickEditText.error = "Invalid nick"
            focusView = nickEditText
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = UserLoginTask(nickStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    inner class UserLoginTask internal constructor(private val username: String, private val password: String)
        : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()

            val resp = backendService.performLogin(requestBody).execute()

            return resp.code() == 200
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null

            if (success!!) {
                finish()
                val myIntent = Intent(this@LoginActivity, MenuActivity::class.java)
                this@LoginActivity.startActivity(myIntent)
            } else {
                passwordEditText.error = "Incorrect password or nick"
                passwordEditText.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
        }
    }
}