package group.ius.englishlearning

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import group.ius.englishlearning.model.WordInfo
import group.ius.englishlearning.retrofit.RetrofitBackend.backendService
import kotlinx.android.synthetic.main.activity_word_summary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WordSummaryActivity : AppCompatActivity() {

    companion object {
        const val WORD_ID = "wordId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_summary)
        getStats(intent.getIntExtra(WORD_ID, 0))
    }

    private fun getStats(wordId: Int) {
        backendService.getWordInfo(wordId).enqueue(
                object : Callback<WordInfo> {
                    override fun onFailure(call: Call<WordInfo>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<WordInfo>, response: Response<WordInfo>) {
                        val info = response.body()!!

                        englishWord.text = "Word:  ${info.englishWord}"
                        translation.text = "Translation: ${info.translation}"
                        levelOfKnowledge.text = "Level of knowledge: ${info.levelOfKnowledge.name}"
                        percentOfKnowledge.text = "Percent of knowledge: ${info.percentOfKnowledge}"
                        successTrainsCount.text = "Success trainings count: ${info.numOfSuccessTrains}"
                        totalNumOfTrains.text = "Total trainings count: ${info.totalNumOfTrains}"
                    }
                }
        )

        changeTranslationButton.setOnClickListener {
            val editText = EditText(this)

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        backendService.changeTranslation(wordId, editText.text.toString()).enqueue(
                                object : Callback<Unit> {
                                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                                        println(t.message)
                                    }

                                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                        getStats(wordId)
                                    }
                                }
                        )
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setView(editText)

            builder.setMessage("Enter new translation")
                    .setPositiveButton("Submit", dialogClickListener)
                    .setNegativeButton("Back", dialogClickListener)
                    .show()
        }

        removeButton.setOnClickListener {

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {

                        backendService.deleteWord(wordId).enqueue(
                                object : Callback<Unit> {
                                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                                        println(t.message)
                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    }

                                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    }
                                }
                        )
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
        }
    }
}
