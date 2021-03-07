package com.example.quiz_app_mvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quiz_app_mvvm.repositories.FirebaseRepo
import com.example.quiz_app_mvvm.model.QuizModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quiz_app_mvvm.model.MyResult
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class QuizListViewModel : ViewModel() {

    private val firebaseRepo = FirebaseRepo()
    private var quizModel = QuizModel()
    private var createdQuizModel = QuizModel()
    private var myResult = MyResult()

    fun getParticipatedQuizOptions(): LiveData<FirestoreRecyclerOptions<QuizModel>> {
        return firebaseRepo.getParticipatedQuizOptions()
    }

    fun getMyCreatedQuizzes(): LiveData<FirestoreRecyclerOptions<QuizModel>> {
        return firebaseRepo.getMyCreatedQuizzes()
    }

    fun getMyResults(): LiveData<FirestoreRecyclerOptions<MyResult>> {
        return firebaseRepo.getMyResults()
    }

    fun getPublicResults(quizID: String): LiveData<FirestoreRecyclerOptions<MyResult>> {
        return firebaseRepo.getPublicResults(quizID)
    }

//    fun getListSizeCheck(): MutableLiveData<Int> {
//        return firebaseRepo.getListSizeCheck()
//    }

// ----------------------------------------------------------------------------------------------

    // These setter and getter are for data transfer from one fragment to another
    fun setQuizData(quizModel: QuizModel) {
        this.quizModel = quizModel
    }

    fun getQuizData(): QuizModel {
        return quizModel
    }

    fun setResult(myResult: MyResult) {
        this.myResult = myResult
    }

    fun getResult(): MyResult {
        return myResult
    }

//    override fun quizListDataAdded(quizList: List<QuizModel>?) {
//        quizListLiveData.value = quizList
//    }
//
//    override fun onError(e: Exception?) {
//        TODO("Not yet implemented")
//    }
}