package group.ius.englishlearning

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import group.ius.englishlearning.model.UserData
import group.ius.englishlearning.retrofit.RetrofitBackend.backendService
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Callback
import retrofit2.Response


class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        backendService.getUserData().enqueue(
                object : Callback<UserData> {
                    override fun onFailure(call: retrofit2.Call<UserData>, t: Throwable) {
                        helloText.text = "Hello, dear user!"
                    }

                    override fun onResponse(call: retrofit2.Call<UserData>, response: Response<UserData>) {
                        helloText.text = "Hello, ${response.body()!!.nickName}!"
                    }
                }
        )

        textEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val wordToTranslate = textEdit.text.toString()
                if (wordToTranslate.length > 1) {

                    val translateIntent = Intent(this, TranslateActivity::class.java)
                    translateIntent.putExtra(TranslateActivity.WORD_TO_TRANSLATE, wordToTranslate)
                    translateIntent.putExtra(TranslateActivity.TO_ENG, ruEnButton.isChecked)

                    textEdit.clearComposingText()
                    startActivity(translateIntent)
                } else {
                    textEdit.error = "Too short"
                    textEdit.requestFocus()
                }
            }

            println("GOGOGOGOGOGOO!!!!")
            true
        }
    }

    fun openInfoEditActivity(view: View) {
        val randomIntent = Intent(this, InfoEditActivity::class.java)

        startActivity(randomIntent)
    }

    fun openDictionaryActivity(view: View) {
        val randomIntent = Intent(this, DictionaryActivity::class.java)

        startActivity(randomIntent)
    }

    fun openTrainingActivity(view: View) {
        val trainingIntent = Intent(this@MenuActivity, TrainingActivity::class.java)
        trainingIntent.putExtra(TrainingActivity.IS_ENG, enRuButton.isChecked)

        startActivity(trainingIntent)
    }
}