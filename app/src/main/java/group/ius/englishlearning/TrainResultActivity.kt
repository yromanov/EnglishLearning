package group.ius.englishlearning

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import group.ius.englishlearning.R
import kotlinx.android.synthetic.main.activity_train_result.*

class TrainResultActivity : AppCompatActivity() {

    companion object {
        const val SUCCESS_RATE = "successRate"
        const val STUDIED_COUNT = "studiedCount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_result)

        val succesRateValue = intent.getDoubleExtra(SUCCESS_RATE, 0.0)
        val studiedCount = intent.getIntExtra(STUDIED_COUNT, 0)

        succesRate.text = "Percentage of right answers: ${succesRateValue}"
        newlyStudied.text = "Newly studied count: ${studiedCount}"
        if (studiedCount > 0) {
            congratulation.text = "Congratulations!"
        } else {
            congratulation.text = "Don't worry! It will be better next time :-)"
        }
    }
}
