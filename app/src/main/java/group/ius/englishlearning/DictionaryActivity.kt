package group.ius.englishlearning

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import group.ius.englishlearning.model.LevelOfKnowledge
import group.ius.englishlearning.model.UserDictonaryEntry
import group.ius.englishlearning.retrofit.RetrofitBackend
import kotlinx.android.synthetic.main.activity_dictionary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DictionaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        RetrofitBackend.backendService.getUserDictionary().enqueue(
                object : Callback<List<UserDictonaryEntry>> {
                    override fun onFailure(call: Call<List<UserDictonaryEntry>>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<List<UserDictonaryEntry>>,
                                            response: Response<List<UserDictonaryEntry>>) {
                        for (entry in response.body()!!) {
                            val button = Button(this@DictionaryActivity)
                            button.text = entry.word

                            button.isClickable = false

                            when (entry.levelOfKnowledge) {
                                LevelOfKnowledge.NOT_STUDIED -> button.setBackgroundColor(Color.RED)
                                LevelOfKnowledge.ON_THE_WAY -> button.setBackgroundColor(Color.CYAN)
                                LevelOfKnowledge.STUDIED -> button.setBackgroundColor(Color.GREEN)
                            }

                            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)

                            dictionaryLayout.addView(button, lp)
                        }
                    }
                }
        )
    }
}
