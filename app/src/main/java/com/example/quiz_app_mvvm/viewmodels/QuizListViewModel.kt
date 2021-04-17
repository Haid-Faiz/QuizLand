package com.example.quiz_app_mvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.example.quiz_app_mvvm.repositories.FirebaseRepo
import com.example.quiz_app_mvvm.model.QuizModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_mvvm.model.MyResult
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.launch

class QuizListViewModel : ViewModel() {

    private val firebaseRepo = FirebaseRepo()
    private var quizModel = QuizModel()
    private var myResult = MyResult()

    private var _participatedQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()
    var participatedQuizOptions: LiveData<FirestoreRecyclerOptions<QuizModel>> = _participatedQuizOptions
    private var _createdQuizOptions: MutableLiveData<FirestoreRecyclerOptions<QuizModel>> = MutableLiveData()
    var createdQuizOptions: LiveData<FirestoreRecyclerOptions<QuizModel>> = _createdQuizOptions
    private var _myResultsOptions: MutableLiveData<FirestoreRecyclerOptions<MyResult>> = MutableLiveData()
    var myResultOptions: LiveData<FirestoreRecyclerOptions<MyResult>> = _myResultsOptions
    private var _publicResultsOptions: MutableLiveData<FirestoreRecyclerOptions<MyResult>> = MutableLiveData()
    var publicResultOptions: LiveData<FirestoreRecyclerOptions<MyResult>> = _publicResultsOptions


    fun getParticipatedQuizOptions() = viewModelScope.launch {
        val quizOptions: FirestoreRecyclerOptions<QuizModel> = firebaseRepo.getParticipateQuizOptions()
        _participatedQuizOptions.postValue(quizOptions)
    }

    fun getMyCreatedQuizzes() = viewModelScope.launch {
        _createdQuizOptions.postValue(firebaseRepo.getMyCreatedQuizzes())
    }

    fun getMyResults() = viewModelScope.launch {
        _myResultsOptions.postValue(firebaseRepo.getMyResults())
    }

    fun getPublicResults(quizID: String) = viewModelScope.launch {
        _publicResultsOptions.postValue(firebaseRepo.getPublicResults(quizID))
    }

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
}