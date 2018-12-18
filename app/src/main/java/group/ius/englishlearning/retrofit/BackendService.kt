package group.ius.englishlearning.retrofit

import group.ius.englishlearning.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface BackendService {
    @POST("perform_login")
    fun performLogin(@Body body: RequestBody): Call<Unit>

    @GET("translation/get")
    fun getTranslations(@Query("word") word: String,
                        @Query("from") from: String,
                        @Query("to") to: String): Call<TranslationResponse>

    @POST("user/perform_registration")
    fun performRegistration(@Body registrationData: RegistrationData): Call<Unit>

    @POST("user/changeNickname")
    fun changeNickname(@Body nickname: NickName): Call<Unit>

    @POST("user/changePassword")
    fun changePassword(@Body pass: Password): Call<Unit>

    @GET("user/userInfo")
    fun getUserData(): Call<UserData>

    @GET("user/dictionary")
    fun getUserDictionary(): Call<List<UserDictonaryEntry>>

    @POST("user/registerChosenTranslation")
    fun registerChosenTranslation(@Query("language") language: String,
                                  @Body chosenTranslation: ChosenTranslation): Call<Unit>

    @POST("user/registerTrainResult")
    fun registerTrainResult(@Body result: Map<String, Boolean>): Call<Unit>

    @GET("user/trainWordsList")
    fun getTrainWordsList(@Query("languageOfTrain") languageOfTrain: String): Call<List<TrainEntry>>
}