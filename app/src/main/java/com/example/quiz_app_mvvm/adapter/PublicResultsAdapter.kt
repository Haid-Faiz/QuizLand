package com.example.quiz_app_mvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz_app_mvvm.databinding.ResultsListItemBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PublicResultsAdapter(
        private val options: FirestoreRecyclerOptions<MyResult>,
        private val clickListenerFunction: (MyResult) -> Unit,
        private val onItemChanged: (itemCount: Int) -> Unit
) : FirestoreRecyclerAdapter<MyResult, PublicResultsAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val resultsListItemBinding = ResultsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        resultsListItemBinding.participantResultListener = participantResultListener
//        resultsListItemBinding.f = this
        resultsListItemBinding.kf = clickListenerFunction

        return ViewHolder(resultsListItemBinding)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        onItemChanged.invoke(itemCount)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: MyResult) {
        holder.resultsListItemBinding.result = model
        holder.resultsListItemBinding.position = position
    }

    class ViewHolder(val resultsListItemBinding: ResultsListItemBinding): RecyclerView.ViewHolder(resultsListItemBinding.root)
}