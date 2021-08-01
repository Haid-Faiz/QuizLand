package com.example.quiz_app_mvvm.repositories

import android.content.Context
import android.net.Uri
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizFragment
import com.example.quiz_app_mvvm.util.Constants.ALL_RESULTS_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.MY_CREATED_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.MY_RESULTS_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.Participated_Quiz_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.QUESTIONS_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.QUIZ_LIST_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.RESULTS_COLLECTION
import com.example.quiz_app_mvvm.util.Constants.USERS_COLLECTION
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuizRepo @Inject constructor(
    db: FirebaseFirestore,
    private var storageRef: StorageReference,
    @ApplicationContext private val context: Context
) : BaseRepo(context) {

    private val user: FirebaseUser = Firebase.auth.currentUser!!
    private val quizListCollection: CollectionReference = db.collection(QUIZ_LIST_COLLECTION)
    private val userCollection = db.collection(USERS_COLLECTION)
    private val resultCollection = db.collection(RESULTS_COLLECTION)


    suspend fun fetchQuestions(userId: String, quizId: String): Resource<QuerySnapshot> =
        safeApiCall {
            val questionsTask: Task<QuerySnapshot> = userCollection
                .document(userId)
                .collection(Participated_Quiz_COLLECTION)
                .document(quizId)
                .collection(QUESTIONS_COLLECTION)
                .get()
            // passing this to safeApiCall
            questionsTask
        }

    suspend fun isQuizExist(quizID: String): Resource<DocumentSnapshot> = safeApiCall {
        val task: Task<DocumentSnapshot> = quizListCollection.document(quizID).get()
        task
    }

    suspend fun joinQuiz(quizId: String): Resource<Void> {
        // firstly retrieve quiz from QuizList
        getQuiz(quizId).let {
            when (it) {
                is Resource.Error -> TODO()
                is Resource.Loading -> TODO()
                is Resource.Success -> {
                    val quiz: QuizModel? = it.data?.toObject(QuizModel::class.java)

                    // Now retrieve question list
                    getQuestions(quizId).let { questions: Resource<QuerySnapshot> ->
                        when (questions) {
                            is Resource.Error -> TODO()
                            is Resource.Loading -> TODO()
                            is Resource.Success -> {
                                val questionList = questions.data!!.documents[0]
                                    .toObject(QuizFragment.QuestionsListModel::class.java)?.questionsList

                                // Now add quiz to MyParticipatedQuiz collection
                                setQuiz(quizId, quiz).let { quiz: Resource<Void> ->
                                    when (quiz) {
                                        is Resource.Error -> TODO()
                                        is Resource.Loading -> TODO()
                                        is Resource.Success -> {
                                            // Now finally add question list to MyParticipatedQuiz collection
                                            setQuestions(
                                                quizId,
                                                questionList
                                            ).let { result: Resource<Void> ->
                                                when (result) {
                                                    is Resource.Error -> TODO()
                                                    is Resource.Loading -> TODO()
                                                    is Resource.Success -> {
                                                        return result
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // FireStore Adapter will automatically update the participated quiz list
    }

    private suspend fun getQuiz(quizId: String) = safeApiCall {
        quizListCollection.document(quizId).get()
    }

    private suspend fun getQuestions(quizId: String) = safeApiCall {
        quizListCollection.document(quizId).collection(QUESTIONS_COLLECTION).get()
    }

    private suspend fun setQuiz(quizId: String, quiz: QuizModel?) = safeApiCall {
        userCollection
            .document(user?.uid!!)
            .collection(Participated_Quiz_COLLECTION)
            .document(quizId)
            .set(quiz!!)
    }

    private suspend fun setQuestions(
        quizId: String,
        questionList: ArrayList<QuestionsModel>?
    ): Resource<Void> =
        safeApiCall {
            val map = HashMap<String, ArrayList<QuestionsModel>?>()
            map["questionsList"] = questionList

            val task: Task<Void> = userCollection
                .document(user?.uid!!)
                .collection(Participated_Quiz_COLLECTION)
                .document(quizId)
                .collection(QUESTIONS_COLLECTION)
                .document()
                .set(map)
            task
        }


    suspend fun getParticipateQuizList(): Resource<QuerySnapshot> = safeApiCall {
        val queryOfParticipatedQuiz: Task<QuerySnapshot> = userCollection
            .document(user?.uid!!)
            .collection(Participated_Quiz_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()

        queryOfParticipatedQuiz
    }

    suspend fun unEnrolQuiz(quizID: String): Resource<Void> {
        // Firstly getting id of question list document of quiz to be unEnrolled
        // because we need to delete both quiz & question list
        getQuestionListDocumentId(quizID).let {
            when (it) {
                is Resource.Error -> TODO()
                is Resource.Loading -> TODO()
                is Resource.Success -> {
                    val questionListID = it.data!!.documents[0].id
                    // now we got the question list id... lets delete it
                    deleteQuestionList(quizID, questionListID).let { isDeleted: Resource<Void> ->
                        when (isDeleted) {
                            is Resource.Error -> TODO()
                            is Resource.Loading -> TODO()
                            is Resource.Success -> {
                                // now deleting the quiz itself
                                deleteParticipatedQuiz(quizID).let { isQuizDeleted: Resource<Void> ->
                                    when (isQuizDeleted) {
                                        is Resource.Error -> TODO()
                                        is Resource.Loading -> TODO()
                                        is Resource.Success -> {
                                            // Also delete corresponding result from My Results
                                            deleteResult(quizID).let { isResultDeleted ->
                                                when (isResultDeleted) {
                                                    is Resource.Error -> TODO()
                                                    is Resource.Loading -> TODO()
                                                    is Resource.Success -> {
                                                        return isResultDeleted
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getQuestionListDocumentId(quizId: String) = safeApiCall {
        userCollection
            .document(user?.uid!!)
            .collection(Participated_Quiz_COLLECTION)
            .document(quizId)
            .collection(QUESTIONS_COLLECTION)
            .get()
    }

    private suspend fun deleteQuestionList(quizId: String, questionListID: String) = safeApiCall {
        userCollection
            .document(user?.uid!!)
            .collection(Participated_Quiz_COLLECTION)
            .document(quizId)
            .collection(QUESTIONS_COLLECTION)
            .document(questionListID)
            .delete()
    }

    private suspend fun deleteParticipatedQuiz(quizId: String) = safeApiCall {
        userCollection
            .document(user?.uid!!)
            .collection(Participated_Quiz_COLLECTION)
            .document(quizId)
            .delete()
    }

    private suspend fun deleteResult(quizId: String) = safeApiCall {
        userCollection.document(user?.uid!!)
            .collection(MY_RESULTS_COLLECTION)
            .document(quizId)
            .delete()
    }

    suspend fun createQuiz(
        quizModel: QuizModel,
        questionsList: ArrayList<QuestionsModel>
    ): Resource<Void> {
        return if (!quizModel.imageUrl.isNullOrBlank()) {
            // uploading the image
            uploadImage(Uri.parse(quizModel.imageUrl)).let {
                when (it) {
                    is Resource.Error -> TODO()
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {
                        downloadImage(it.data!!).let { uri: Resource<Uri> ->
                            return when (uri) {
                                is Resource.Error -> TODO()
                                is Resource.Loading -> TODO()
                                is Resource.Success -> {
                                    addQuiz(quizModel, questionsList)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            addQuiz(quizModel, questionsList)
        }
    }

    private suspend fun addQuiz(
        quizModel: QuizModel,
        questionsList: ArrayList<QuestionsModel>
    ): Resource<Void> {
        val docID: String = quizListCollection.document().id
        // setting the quizID
        quizModel.quiz_id = docID
        // now upload the quizModel in public collection
        quizListCollection.document(docID).set(quizModel)
        // Adding questions to public & user collection
        return addQuestions(docID, questionsList, quizModel)
    }

    private suspend fun uploadImage(imageUri: Uri): Resource<UploadTask.TaskSnapshot> =
        safeApiCall {
            val filePath = storageRef.child("QuizImages").child(imageUri.lastPathSegment.toString())
            filePath.putFile(imageUri)
        }

    private suspend fun downloadImage(data: UploadTask.TaskSnapshot) = safeApiCall {
        data.storage.downloadUrl
    }

    private suspend fun addQuestions(
        docID: String,
        questionsList: ArrayList<QuestionsModel>,
        quizModel: QuizModel
    ): Resource<Void> {

        safeApiCall {
            // adding questions list to public collection
            val hashMap = HashMap<String, ArrayList<QuestionsModel>>()
            hashMap["questionsList"] = questionsList      // Kotlin style
            quizListCollection
                .document(docID)
                .collection(QUESTIONS_COLLECTION)
                .document()
                .set(hashMap)
        }.let {
            when (it) {
                is Resource.Error -> return it
                is Resource.Loading -> TODO()
                is Resource.Success -> {
                    safeApiCall {
                        // adding questions list user collection
                        val hashMap = HashMap<String, ArrayList<QuestionsModel>>()
                        hashMap["questionsList"] = questionsList

                        userCollection.document(user?.uid!!)
                            .collection(MY_CREATED_COLLECTION)
                            .document(docID)
                            .collection(QUESTIONS_COLLECTION)
                            .document()
                            .set(hashMap)
                    }.let { result: Resource<Void> ->
                        return when (result) {
                            is Resource.Error -> result
                            is Resource.Loading -> TODO()
                            is Resource.Success -> safeApiCall {
                                // Also add quiz to user account in MyCreatedQuiz collection
                                userCollection.document(user?.uid!!)
                                    .collection(MY_CREATED_COLLECTION)
                                    .document(docID)
                                    .set(quizModel)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getMyCreatedQuizzes(): Resource<QuerySnapshot> = safeApiCall {
        val queryOfCreatedQuiz: Task<QuerySnapshot> = userCollection
            .document(user?.uid!!)
            .collection(MY_CREATED_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()

        queryOfCreatedQuiz
    }

    suspend fun uploadResult(quizId: String, myResult: MyResult): Resource<Void> {
        // Adding result in User > MyResults
        safeApiCall {
            userCollection.document(user?.uid!!)
                .collection(MY_RESULTS_COLLECTION)
                .document(quizId)
                .set(myResult)
        }.let {
            return when (it) {
                is Resource.Error -> TODO()
                is Resource.Loading -> TODO()
                is Resource.Success -> safeApiCall {
                    // now also add result in Result collection > Quiz id
                    resultCollection.document(quizId)
                        .collection(ALL_RESULTS_COLLECTION)
                        .document(user!!.uid)
                        .set(myResult)
                }
            }
        }
    }

    suspend fun updateParticipationStatus(quizId: String) = safeApiCall {
        userCollection
            .document(user?.uid!!)
            .collection("MyParticipatedQuiz")
            .document(quizId)
            .update("participated", true)
    }

    suspend fun getPublicResults(quizID: String): Resource<QuerySnapshot> = safeApiCall {
        val query = resultCollection
            .document(quizID)
            .collection(ALL_RESULTS_COLLECTION)
            .orderBy("marksScored", Query.Direction.DESCENDING)
            .get()
        query
    }

    suspend fun getMyResults(): Resource<QuerySnapshot> = safeApiCall {
        val query = userCollection
            .document(user?.uid!!)
            .collection(MY_RESULTS_COLLECTION)
            .get()
        query
    }

    //------------------------------------------------------------------------------

    suspend fun deleteCreatedQuiz(quizID: String): Resource<Void> {

        userCollection.document(user?.uid!!)
            .collection(MY_CREATED_COLLECTION)
            .document(quizID)
            .collection(QUESTIONS_COLLECTION)
            .get()
            .await()
            .documents[0]
            .id.also {
                userCollection.document(user.uid)
                    .collection(MY_CREATED_COLLECTION)
                    .document(quizID)
                    .collection(QUESTIONS_COLLECTION)
                    .document(it)
                    .delete().isSuccessful.also {
                        // Now also delete completely from QuizList collection
                        userCollection.document(user.uid)
                            .collection(MY_CREATED_COLLECTION)
                            .document(quizID)
                            .delete()

                        quizListCollection
                            .document(quizID)
                            .collection(QUESTIONS_COLLECTION)
                            .get()
                            .await()
                            .documents[0]
                            .id.let { questionsId: String ->
                                quizListCollection
                                    .document(quizID)
                                    .collection(QUESTIONS_COLLECTION)
                                    .document(questionsId)
                                    .delete().isSuccessful.let {
                                        return safeApiCall {
                                            quizListCollection
                                                .document(quizID)
                                                .delete()
                                        }
                                    }
                            }
                    }
            }
    }
}