package com.example.quiz_app_mvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz_app_mvvm.databinding.CreatedQuizItemBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray

class CreatedQuizzesAdapter(
        options: FirestoreRecyclerOptions<QuizModel>,
        private val onCreatedQuizItemClicked: OnCreatedQuizItemClicked,
//        private val f: (itemCount: Int) -> Unit
) : FirestoreRecyclerAdapter<QuizModel, CreatedQuizzesAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val createdQuizItemBinding = CreatedQuizItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        createdQuizItemBinding.onCreatedQuizItemClicked = onCreatedQuizItemClicked
        return ViewHolder(createdQuizItemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuizModel) {
        holder.createdQuizItemBinding.quiz = model
        holder.createdQuizItemBinding.position = position
//        snapshots.get(holder.adapterPosition).quiz_id
    }

    override fun onDataChanged() {
        super.onDataChanged()

//        f(itemCount)  // invoking HOF
        onCreatedQuizItemClicked.onListItemChanged(itemCount)
    }

    class ViewHolder(val createdQuizItemBinding: CreatedQuizItemBinding)
        : RecyclerView.ViewHolder(createdQuizItemBinding.root)


    interface OnCreatedQuizItemClicked {
        fun onGetResultClicked(position: Int)
        fun onDeleteClicked(position: Int)
        fun onShareQuizClicked(position: Int)
        fun onListItemChanged(itemCount: Int)
    }
}