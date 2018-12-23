package group.ius.englishlearning

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import group.ius.englishlearning.TrainResultActivity.Companion.STUDIED_COUNT
import group.ius.englishlearning.TrainResultActivity.Companion.SUCCESS_RATE
import group.ius.englishlearning.model.RegisterTrainResultResponse
import group.ius.englishlearning.model.TrainEntry
import group.ius.englishlearning.retrofit.RetrofitBackend
import kotlinx.android.synthetic.main.activity_training.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TrainingActivity : AppCompatActivity() {

    companion object {
        private val helpCollection = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        private val borderBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(5, Color.MAGENTA)
        }

        const val IS_ENG = "is_eng"
    }

    private var listOfButtons: List<Button> = emptyList()
    private var defaultBack: Drawable? = null

    private var listOfTrainWords: List<TrainEntry> = emptyList()
    private var index = 0
    private var successCount = 0.0

    private var chosenTranslation: String? = null

    private var resulMap: MutableMap<String, Boolean> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        listOfButtons = listOf(buttonL1, buttonL2, buttonL3, buttonR1, buttonR2, buttonR3)
        defaultBack = buttonR3.background
        resulMap = mutableMapOf()

        val isEng = intent.getBooleanExtra(TrainingActivity.IS_ENG, true)

        RetrofitBackend.backendService.getTrainWordsList(if (isEng) "en" else "ru").enqueue(
                object : Callback<List<TrainEntry>> {
                    override fun onFailure(call: Call<List<TrainEntry>>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<List<TrainEntry>>, response: Response<List<TrainEntry>>) {

                        val body = response.body()
                        if (body != null) {
                            listOfTrainWords = response.body()!!
                            setListeners()
                            updateWords()
                        } else {
                            Toast.makeText(this@TrainingActivity,
                                    "Not enough words in dictionary",
                                    Toast.LENGTH_SHORT).apply { setGravity(Gravity.CENTER, 0, 300) }
                                    .show()

                            Handler().postDelayed({ finish() }, 2000)
                        }
                    }
                }
        )
    }

    private fun setListeners() {
        listOfButtons.forEach { button: Button ->
            button.setOnClickListener {
                button.background = borderBackground
                chosenTranslation = button.text.toString()
            }
        }
    }

    private fun resetButtons() {
        println("reset!")
        listOfButtons.forEach {
            it.background = defaultBack!!
        }
    }

    private fun updateWords() {
        if (index == 9) {
            RetrofitBackend.backendService.registerTrainResult(resulMap).enqueue(object : Callback<RegisterTrainResultResponse> {
                override fun onFailure(call: Call<RegisterTrainResultResponse>, t: Throwable) {}

                override fun onResponse(call: Call<RegisterTrainResultResponse>, response: Response<RegisterTrainResultResponse>) {

                    val result = response.body()!!

                    val resultIntent = Intent(this@TrainingActivity, TrainResultActivity::class.java)

                    resultIntent.putExtra(SUCCESS_RATE, successCount / 10)
                    resultIntent.putExtra(STUDIED_COUNT, result.newlyStudied)

                    startActivity(resultIntent)
                    finish()
                }
            })
        }

        resetButtons()

        val entry = listOfTrainWords[index++]

        trainWord.text = entry.word

        val randomIndexies = helpCollection.shuffled().subList(0, 5)

        val shuffledAnswersIterator = randomIndexies.map { entry.wrongTranslations[it] }
                .plus(entry.rightTranslation).shuffled().listIterator()

        listOfButtons.forEach { button: Button ->
            button.text = shuffledAnswersIterator.next()
        }

        submitButton.setOnClickListener {
            if (chosenTranslation != null) {
                resetButtons()

                val wasRight = chosenTranslation == entry.rightTranslation

                if (wasRight) {
                    successCount++
                }

                Toast.makeText(this@TrainingActivity,
                        if (wasRight) "Right" else "Wrong",
                        Toast.LENGTH_SHORT).apply { setGravity(Gravity.CENTER, 0, 300) }
                        .show()

                resulMap[entry.wordId.toString()] = wasRight

                chosenTranslation = null

                Handler().postDelayed({ updateWords() }, 1500)
            }
        }
    }
}