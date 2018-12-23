package group.ius.englishlearning

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import group.ius.englishlearning.model.LevelOfKnowledge
import group.ius.englishlearning.model.Summary
import group.ius.englishlearning.retrofit.RetrofitBackend
import kotlinx.android.synthetic.main.activity_summary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        RetrofitBackend.backendService.getSummary().enqueue(
                object : Callback<Summary> {
                    override fun onFailure(call: Call<Summary>, t: Throwable) {
                        println(t.message)
                    }

                    override fun onResponse(call: Call<Summary>, response: Response<Summary>) {
                        val summary = response.body()!!

                        notStudied.text = "Not studied: ${summary.levelKnowledgeMap[LevelOfKnowledge.NOT_STUDIED]}"
                        onTheWay.text = "On the way: ${summary.levelKnowledgeMap[LevelOfKnowledge.ON_THE_WAY]}"
                        studied.text = "Studied: ${summary.levelKnowledgeMap[LevelOfKnowledge.STUDIED]}"
                        numberOflastWeek.text = "Words added last week: ${summary.numOfInsertedLastWeek}"
                        numberOfTrains.text = "Total number of trainings: ${summary.totalNumOfTrains}"
                        numberOfWords.text = "Total number of words: ${summary.totalNumOfWords}"
                    }
                }
        )
    }
}
