package group.ius.englishlearning.model

data class UserDictonaryEntry(val levelOfKnowledge: LevelOfKnowledge,
                              val translation: String,
                              val wordId: Int,
                              val word: String)