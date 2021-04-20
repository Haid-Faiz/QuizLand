package com.example.quiz_app_mvvm.daos

import android.net.Uri
import android.util.Log
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.views.fragments.QuizFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class QuizDao(private val uploadedCallBack: UploadedCallBack? = null) {

    val user = Firebase.auth.currentUser
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val quizListCollection: CollectionReference = db.collection("QuizList")
    val userCollection = db.collection("Users")
    val resultCollection = db.collection("Results")

    // Firebase Storage
    private var storageRef: StorageReference = FirebaseStorage.getInstance().reference

    fun createQuiz(quizModel: QuizModel, questionsList: ArrayList<QuestionsModel>) {

        GlobalScope.launch(Dispatchers.IO) {
            // uploading the image

            if (!quizModel.imageUrl.isNullOrBlank())
                quizModel.imageUrl = uploadImage(Uri.parse(quizModel.imageUrl))

            // got the doc id first
            val docID: String = quizListCollection.document().id
            // setting the quizID
            quizModel.quiz_id = docID
            // now upload the quizModel
            quizListCollection.document(docID).set(quizModel)
            addQuestions(docID, questionsList)

            // Also add quiz to user account in MyCreatedQuiz collection
            userCollection.document(user?.uid!!)
                    .collection("MyCreatedQuiz")
                    .document(docID)
                    .set(quizModel)

            val hashMap = HashMap<String, ArrayList<QuestionsModel>>()
            hashMap["questionsList"] = questionsList

            userCollection.document(user.uid)
                    .collection("MyCreatedQuiz")
                    .document(docID)
                    .collection("Questions")
                    .document()
                    .set(hashMap)
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String {

        val filePath = storageRef.child("QuizImages").child(imageUri.lastPathSegment.toString())
        return filePath.putFile(imageUri).await().storage.downloadUrl.await().toString()
//                .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
//            if (task.isSuccessful){
//                task.result?.storage?.downloadUrl?.addOnCompleteListener { t: Task<Uri> ->
//                    if (t.isSuccessful){
//
//                    }
//                }
//            }
//        }
    }

//    private fun uploadImage(imageUri: Uri?): String {
////        val taskOne: UploadTask.TaskSnapshot = filePath.putFile(imageUri).await()
////        val taskTwo: Uri = taskOne.storage.downloadUrl.await()
////        val downloadURL = taskTwo.toString()
//        var returnURL = ""
//        imageUri?.let {
//            GlobalScope.launch(Dispatchers.IO) {
//                val filePath = storageRef.child("QuizImages")
//                        .child(it.lastPathSegment.toString())
//
//                val downloadedUrl = filePath.putFile(it).await().storage.downloadUrl.await().toString()
//                Log.d("superMan3", "uploadImage: $returnURL")
//                withContext(Dispatchers.Main) {
//                    returnURL = downloadedUrl
//                }
//            }
//            Log.d("superMan", "uploadImage: $returnURL")
//        }
//        Log.d("superMan2", "uploadImage: $returnURL")
//        return returnURL
//    }

    private fun addQuestions(docID: String, questionsList: ArrayList<QuestionsModel>) {

        val hashMap = HashMap<String, ArrayList<QuestionsModel>>()
        hashMap["questionsList"] = questionsList      // Kotlin style

        quizListCollection
                .document(docID)
                .collection("Questions")
                .document()
                .set(hashMap).addOnCompleteListener {
                    val isAdded = it.isSuccessful
                    if (isAdded) {
                        uploadedCallBack?.isUploaded(isAdded, docID)
                    }
                }
    }

    fun joinQuiz(uniqueQuizId: String) {

        GlobalScope.launch(Dispatchers.IO) {

//            val isQuizExists = quizListCollection
//                    .document(uniqueQuizId)
//                    .get()
//                    .await()
//                    .exists()

            // firstly retrieve quiz from QuizList
            val quiz: QuizModel? = quizListCollection
                    .document(uniqueQuizId)
                    .get()
                    .await()
                    .toObject(QuizModel::class.java)

            val questionsListModel: QuizFragment.QuestionsListModel? = quizListCollection
                    .document(uniqueQuizId)
                    .collection("Questions")
                    .get()
                    .await()
                    .documents[0]
                    .toObject(QuizFragment.QuestionsListModel::class.java)

            val questionList: ArrayList<QuestionsModel>? = questionsListModel?.questionsList

            // Now add quiz and quizModel to MyParticipatedQuiz collection
            userCollection
                    .document(user?.uid!!)
                    .collection("MyParticipatedQuiz")
                    .document(uniqueQuizId)
                    .set(quiz!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val map = HashMap<String, ArrayList<QuestionsModel>?>()
                            map["questionsList"] = questionList

                            userCollection
                                    .document(user.uid)
                                    .collection("MyParticipatedQuiz")
                                    .document(uniqueQuizId)
                                    .collection("Questions")
                                    .document()
                                    .set(map).addOnCompleteListener { task: Task<Void> ->
                                        if (task.isSuccessful) {
                                            uploadedCallBack?.isUploaded(true)
                                        }
                                    }
                        } else {
                            uploadedCallBack?.isUploaded(false)
                        }
                    }
            // FireStore Adapter will automatically update the participated quiz list
        }
    }

    fun unEnrolQuiz(quizID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userCollection
                    .document(user?.uid!!)
                    .collection("MyParticipatedQuiz")
                    .document(quizID)
                    .collection("Questions")
                    .get()
                    .await()
                    .documents
                    .get(0)
                    .id.also {
                        userCollection
                                .document(user?.uid!!)
                                .collection("MyParticipatedQuiz")
                                .document(quizID)
                                .collection("Questions")
                                .document(it)
                                .delete().isSuccessful.also {
                                    userCollection
                                            .document(user?.uid!!)
                                            .collection("MyParticipatedQuiz")
                                            .document(quizID)
                                            .delete()
                                }
                    }
//                    .delete().addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            uploadedCallBack?.isDeleted(true)
//                        }
//                    }

            // Also delete corresponding result from My Results
            userCollection.document(user.uid)
                    .collection("MyResults")
                    .document(quizID)
                    .delete()
        }
    }

    fun deleteQuiz(quizID: String) {
        //This method will be called when Admin will delete the quiz
        CoroutineScope(Dispatchers.IO).launch {

            userCollection.document(user?.uid!!)
                    .collection("MyCreatedQuiz")
                    .document(quizID)
                    .collection("Questions")
                    .get()
                    .await()
                    .documents
                    .get(0)
                    .id.also {
                        Log.d("checkD0", "id: $it")
                        userCollection.document(user?.uid!!)
                                .collection("MyCreatedQuiz")
                                .document(quizID)
                                .collection("Questions")
                                .document(it)
                                .delete().isSuccessful.also { isDeleted: Boolean ->
                                    // Now also delete completely from QuizList collection
                                    Log.d("checkD1", "deleteQuiz: $isDeleted")

                                    userCollection.document(user?.uid!!)
                                            .collection("MyCreatedQuiz")
                                            .document(quizID)
                                            .delete()

                                    quizListCollection
                                            .document(quizID)
                                            .collection("Questions")
                                            .get()
                                            .await()
                                            .documents
                                            .get(0)
                                            .id.let { questionsId: String ->
                                                quizListCollection
                                                        .document(quizID)
                                                        .collection("Questions")
                                                        .document(questionsId)
                                                        .delete().isSuccessful.let {
                                                            quizListCollection
                                                                    .document(quizID)
                                                                    .delete()
                                                        }
                                            }
                                }
                    }

//                    .let {
//                        Log.d("checkD0", "deleteQuiz: $it")
//                        if (it) {
//                            Log.d("checkD1", "deleteQuiz: $it")
//                            // Now also delete from QuizList collection
//                            quizListCollection.document(quizID).delete()
////                                    .isComplete.let { isDeleted: Boolean ->
////
////                                withContext(Dispatchers.Main) {
////                                    uploadedCallBack?.isDeleted(isDeleted)
////                                }
////                            }
//                        }
//                    }
        }
    }

    fun setResult(quizId: String, myResult: MyResult) {

        GlobalScope.launch(Dispatchers.IO) {
            // Adding result in User > MyResults
            userCollection.document(user?.uid!!)
                    .collection("MyResults")
                    .document(quizId)
                    .set(myResult)

            // now also add result in Result collection > Quiz id
            resultCollection.document(quizId)
                    .collection("AllResults")
                    .document(user.uid)
                    .set(myResult)
        }
    }

    fun updateParticipation(quizId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            userCollection
                    .document(user?.uid!!)
                    .collection("MyParticipatedQuiz")
                    .document(quizId)
                    .update("participated", true)
        }
    }

    interface UploadedCallBack {
        fun isUploaded(isAdded: Boolean, docID: String = "")
        fun isDeleted(isDeleted: Boolean)
    }
}