package group.ius.englishlearning.model

data class WordInfo(val englishWord: String,
                    val levelOfKnowledge: LevelOfKnowledge,
                    val numOfSuccessTrains: Int,
                    val percentOfKnowledge: Int,
                    val totalNumOfTrains: Int,
                    val translation: String)