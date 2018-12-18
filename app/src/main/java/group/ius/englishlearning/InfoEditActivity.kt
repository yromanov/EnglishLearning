package group.ius.englishlearning

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import group.ius.englishlearning.model.NickName
import group.ius.englishlearning.model.Password
import group.ius.englishlearning.model.RegistrationData
import group.ius.englishlearning.model.UserData
import group.ius.englishlearning.retrofit.RetrofitBackend
import group.ius.englishlearning.retrofit.RetrofitBackend.backendService
import group.ius.englishlearning.utils.isValidNick
import group.ius.englishlearning.utils.isValidPassword
import kotlinx.android.synthetic.main.activity_info_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoEditActivity : AppCompatActivity() {

    companion object {
        const val IS_REG = "is_reg"
    }

    private var mAuthTask: InfoEditActivity.ChangeCredentialsTask? = null

    private var isReg = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_edit)

        isReg = intent.getBooleanExtra(IS_REG, false)

        if (!isReg) {
            RetrofitBackend.backendService.getUserData().enqueue(
                    object : Callback<UserData> {
                        override fun onFailure(call: Call<UserData>, t: Throwable) {
                            println(t.message)
                        }

                        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                            val userData = response.body()!!

                            nickInputEditText.setText(userData.nickName)
                            emailInputEditText.setText(userData.email)
                            firstNameInputEditText.setText(userData.firstName)
                            secondNameInputEditText.setText(userData.secondName)
                        }
                    }
            )
        }

        saveButton.setOnClickListener { attemptChange() }
    }

    private fun attemptChange() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        nickInputEditText.error = null
        passInputEditText.error = null

        // Store values at the time of the login attempt.
        val nickStr = nickInputEditText.text.toString()
        val passwordStr = passInputEditText.text.toString()

        var cancel = false
        var focusView: View? = null

        if (isReg) {
            if (!isValidPassword(passwordStr)) {
                passInputEditText.error = "Invalid password"
                focusView = passInputEditText
                cancel = true
            }
        }

        if (!isValidNick(nickStr)) {
            nickInputEditText.error = "Invalid nick"
            focusView = nickInputEditText
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = ChangeCredentialsTask(nickStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    inner class ChangeCredentialsTask internal constructor(private val nick: String, private val password: String)
        : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            if (isReg) {
                val registrationData = RegistrationData(nick,
                        emailInputEditText.text.toString(),
                        password,
                        firstNameInputEditText.text.toString(),
                        secondNameInputEditText.text.toString())

                RetrofitBackend.backendService.performRegistration(registrationData).enqueue(
                        object : Callback<Unit> {
                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                println(t.message)
                            }

                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                        }
                )
            } else {
                val newNick = NickName(nickInputEditText.text.toString())

                backendService.changeNickname(newNick).enqueue(object : Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                })

                val newPass = Password(passInputEditText.text.toString())

                if (newPass.newPassword.isNotEmpty()) {
                    backendService.changePassword(newPass).enqueue(
                            object : Callback<Unit> {
                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    println(t.message)
                                }

                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                            }
                    )
                }
            }
            return true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null

            if (success!!) {
                finish()
            } else {
                nickInputEditText.error = "Nick already exists."
                nickInputEditText.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
        }
    }
}
