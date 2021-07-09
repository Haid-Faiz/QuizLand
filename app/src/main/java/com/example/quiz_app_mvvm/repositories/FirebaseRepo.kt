package com.example.quiz_app_mvvm.repositories

import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseRepo {

    private val quizDao = QuizRepo()

    suspend fun getParticipateQuizOptions(): FirestoreRecyclerOptions<QuizModel> {

        val queryOfParticipatedQuiz: Query = quizDao.userCollection
                .document(quizDao.user?.uid!!)
                .collection("MyParticipatedQuiz")
                .orderBy("createdAt", Query.Direction.DESCENDING)

        // Returning firebase recycler option of QuizModel type
        return FirestoreRecyclerOptions.Builder<QuizModel>()
                .setQuery(queryOfParticipatedQuiz, QuizModel::class.java)
                .build()
    }

    suspend fun getMyCreatedQuizzes(): FirestoreRecyclerOptions<QuizModel> {
        val queryOfCreatedQuiz = quizDao.userCollection
                .document(quizDao.user?.uid!!)
                .collection("MyCreatedQuiz")
                .orderBy("createdAt", Query.Direction.DESCENDING)

        // Returning firebase recycler option of QuizModel type
        return FirestoreRecyclerOptions.Builder<QuizModel>()
                .setQuery(queryOfCreatedQuiz, QuizModel::class.java)
                .build()
    }

    suspend fun getMyResults(): FirestoreRecyclerOptions<MyResult> {

        val query: Query = quizDao.userCollection
                .document(quizDao.user?.uid!!)
                .collection("MyResults")
                .get()
                .await()
                .query

        return FirestoreRecyclerOptions.Builder<MyResult>()
                .setQuery(query, MyResult::class.java)
                .build()
    }

    suspend fun getPublicResults(quizID: String): FirestoreRecyclerOptions<MyResult> {

        val query = quizDao.resultCollection
                .document(quizID)
                .collection("AllResults")
                .orderBy("marksScored", Query.Direction.DESCENDING)
//                    .get()
//                    .await()
//                    .query

        return FirestoreRecyclerOptions.Builder<MyResult>()
                .setQuery(query, MyResult::class.java)
                .build()
    }

//    private val quizListRef: Query = database.collection("QuizList").whereEqualTo("visibility", "public")

//    val quizData: Unit get() {
//            quizListRef.get().addOnCompleteListener { task ->
//                if (task.isSuccessful)
//                    onFirestoreTaskComplete.quizListDataAdded(task.result!!.toObjects(QuizModel::class.java))
//                else onFirestoreTaskComplete.onError(task.exception)
//            }
//        }
}