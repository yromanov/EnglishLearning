package group.ius.englishlearning.model


data class TrainEntry(val wordId: Int,
                      val word: String,
                      val rightTranslation: String,
                      val wrongTranslations: List<String>)