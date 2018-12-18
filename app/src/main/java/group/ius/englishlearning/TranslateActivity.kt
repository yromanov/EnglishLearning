package group.ius.englishlearning

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import group.ius.englishlearning.model.ChosenTranslation
import group.ius.englishlearning.model.TranslationResponse
import group.ius.englishlearning.retrofit.RetrofitBackend.backendService
import kotlinx.android.synthetic.main.activity_translate.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TranslateActivity : AppCompatActivity() {

    companion object {
        const val WORD_TO_TRANSLATE = "word_to_translate"
        const val TO_ENG = "to_eng"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        getTranslations()
    }

    private fun getTranslations() {
        val word = intent.getStringExtra(WORD_TO_TRANSLATE)
        findViewById<TextView>(R.id.translatedWord).text = word

        val toEng = intent.getBooleanExtra(TO_ENG, true)

        val from = if (toEng) "ru" else "en"
        val to = if (toEng) "en" else "ru"

        backendService.getTranslations(word, from, to).enqueue(
                object : Callback<TranslationResponse> {
                    override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<TranslationResponse>, response: Response<TranslationResponse>) {
                        val body = response.body()

                        if (body != null) {
                            for (str in body.translations) {
                                val button = Button(this@TranslateActivity)
                                button.text = str.translation

                                button.setOnClickListener {

                                    finish()
                                    val chosenTranslatiion = ChosenTranslation(word, str)

                                    backendService.registerChosenTranslation(
                                            if (!toEng) "en" else "ru",
                                            chosenTranslatiion)
                                            .enqueue(object : Callback<Unit> {
                                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}

                                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                                    println(t.message)
                                                }
                                            })

                                }

                                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT)

                                translationsLayout.addView(button, lp)
                            }
                        } else {
                            val button = Button(this@TranslateActivity)
                            button.text = "Translations not found"
                            button.isClickable = false

                            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)

                            translationsLayout.addView(button, lp)
                        }
                    }
                }
        )
    }
}