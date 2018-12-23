package group.ius.englishlearning.model


data class Summary(val levelKnowledgeMap: Map<LevelOfKnowledge, Int>,
                   val numOfInsertedLastWeek: Int,
                   val totalNumOfTrains: Int,
                   val totalNumOfWords: Int)