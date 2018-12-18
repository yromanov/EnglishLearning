package group.ius.englishlearning.model


data class TranslationResponse(val text: String,
                               val translations: List<Translation>)