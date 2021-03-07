package com.example.quiz_app_mvvm.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FirebaseRepo {

    private val quizDao = QuizDao()

    //    private var liveListSize: MutableLiveData<Int> = MutableLiveData()
    private var liveQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()
    private var liveCreatedQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()
    private var liveMyResultsOptions: MutableLiveData<FirestoreRecyclerOptions<MyResult>> = MutableLiveData()
    private var livePublicResultsOptions: MutableLiveData<FirestoreRecyclerOptions<MyResult>> = MutableLiveData()

    fun getParticipatedQuizOptions(): LiveData<FirestoreRecyclerOptions<QuizModel>> {

        GlobalScope.launch(Dispatchers.IO) {
            val queryOfParticipatedQuiz = quizDao.userCollection
                    .document(quizDao.user?.uid!!)
                    .collection("MyParticipatedQuiz")
                    .orderBy("createdAt", Query.Direction.DESCENDING)

            val options: FirestoreRecyclerOptions<QuizModel> = FirestoreRecyclerOptions.Builder<QuizModel>()
                    .setQuery(queryOfParticipatedQuiz, QuizModel::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                liveQuizOptions.value = options
            }
        }
        return liveQuizOptions
    }

    fun getMyCreatedQuizzes(): LiveData<FirestoreRecyclerOptions<QuizModel>> {

        GlobalScope.launch(Dispatchers.IO) {

            val queryOfCreatedQuiz = quizDao.userCollection
                    .document(quizDao.user?.uid!!)
                    .collection("MyCreatedQuiz")
                    .orderBy("createdAt", Query.Direction.DESCENDING)

            val options: FirestoreRecyclerOptions<QuizModel> = FirestoreRecyclerOptions.Builder<QuizModel>()
                    .setQuery(queryOfCreatedQuiz, QuizModel::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                liveCreatedQuizOptions.value = options
            }
        }
        return liveCreatedQuizOptions
    }

    fun getMyResults(): LiveData<FirestoreRecyclerOptions<MyResult>> {

        GlobalScope.launch(Dispatchers.IO) {
            val query: Query = quizDao.userCollection
                    .document(quizDao.user?.uid!!)
                    .collection("MyResults")
                    .get()
                    .await()
                    .query

            val options: FirestoreRecyclerOptions<MyResult> = FirestoreRecyclerOptions.Builder<MyResult>()
                    .setQuery(query, MyResult::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                liveMyResultsOptions.value = options
            }
        }
        return liveMyResultsOptions
    }

    fun getPublicResults(quizID: String): LiveData<FirestoreRecyclerOptions<MyResult>> {

        GlobalScope.launch(Dispatchers.IO){
            val query = quizDao.resultCollection
                    .document(quizID)
                    .collection("AllResults")
                    .orderBy("marksScored", Query.Direction.DESCENDING)
//                    .get()
//                    .await()
//                    .query

            val options = FirestoreRecyclerOptions.Builder<MyResult>()
                    .setQuery(query, MyResult::class.java)
                    .build()

            withContext(Dispatchers.Main) {
                livePublicResultsOptions.value = options
            }
        }
        return livePublicResultsOptions
    }


    ///////////////////////    val queryOfParticipatedQuiz = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)


    /////////////    private CollectionReference quizListRef = database.collection("QuizList");
//    private val quizListRef: Query = database.collection("QuizList").whereEqualTo("visibility", "public")


//    val quizData: Unit get() {
//            quizListRef.get().addOnCompleteListener { task ->
//                if (task.isSuccessful)
//                    onFirestoreTaskComplete.quizListDataAdded(task.result!!.toObjects(QuizModel::class.java))
//                else onFirestoreTaskComplete.onError(task.exception)
//            }
//        }

//    interface OnFirestoreTaskComplete {
//        fun quizListDataAdded(quizList: List<QuizModel>?)
//        fun onError(e: Exception?)
//    }
}