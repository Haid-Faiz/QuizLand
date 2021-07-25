package com.example.quiz_app_mvvm.ui.quiz

import androidx.lifecycle.ViewModel
import com.example.quiz_app_mvvm.model.QuizModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.repositories.QuizRepo
import com.example.quiz_app_mvvm.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepo: QuizRepo
) : ViewModel() {

    private var quizModel = QuizModel()
    private var myResult = MyResult()

    // QuerySnapshot
    private var _questions: MutableLiveData<Resource<QuerySnapshot>> = MutableLiveData()
    var questions: LiveData<Resource<QuerySnapshot>> = _questions

    // QuerySnapshot
    private var _quizList: MutableLiveData<Resource<QuerySnapshot>> = MutableLiveData()
    var quizList: LiveData<Resource<QuerySnapshot>> = _quizList

    // Result QuerySnapshot
    private var _resultList: MutableLiveData<Resource<QuerySnapshot>> = MutableLiveData()
    var resultList: LiveData<Resource<QuerySnapshot>> = _resultList


    fun getQuestions(userId: String, quizID: String) = viewModelScope.launch {
        _questions.postValue(quizRepo.fetchQuestions(userId, quizID))
    }

    suspend fun isQuizExist(quizID: String): Resource<DocumentSnapshot> {
        return quizRepo.isQuizExist(quizID)
    }

    suspend fun joinQuiz(quizID: String): Resource<Void> {
        return quizRepo.joinQuiz(quizID)
    }

    fun getParticipatedQuizList() = viewModelScope.launch {
        val quizOptions: Resource<QuerySnapshot> = quizRepo.getParticipateQuizList()
        _quizList.postValue(quizOptions)
    }

    suspend fun unEnrolQuiz(quizID: String): Resource<Void> {
        return quizRepo.unEnrolQuiz(quizID)
    }


    suspend fun createQuiz(
        quizModel: QuizModel,
        questionsList: ArrayList<QuestionsModel>
    ): Resource<Void> {
        return quizRepo.createQuiz(quizModel, questionsList)
    }

    suspend fun deleteCreatedQuiz(quizId: String): Resource<Void> {
        return quizRepo.deleteCreatedQuiz(quizId)
    }

    fun getMyCreatedQuizzes() = viewModelScope.launch {
        _quizList.postValue(quizRepo.getMyCreatedQuizzes())
    }

    fun updateParticipationStatus(quizId: String) = viewModelScope.launch {
        quizRepo.updateParticipationStatus(quizId)
    }

    fun uploadResult(quizId: String, myResult: MyResult) = viewModelScope.launch {
        quizRepo.uploadResult(quizId, myResult)
    }

    fun getMyResults() = viewModelScope.launch {
        _resultList.postValue(Resource.Loading())
        _resultList.postValue(quizRepo.getMyResults())
    }

    fun getPublicResults(quizID: String) = viewModelScope.launch {
        _resultList.postValue(Resource.Loading())
//        _resultList.value = null
        _resultList.postValue(quizRepo.getPublicResults(quizID))
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