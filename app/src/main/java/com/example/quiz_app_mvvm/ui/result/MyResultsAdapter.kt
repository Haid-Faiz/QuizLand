package com.example.quiz_app_mvvm.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz_app_mvvm.databinding.MyResultItemBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MyResultsAdapter(
    options: FirestoreRecyclerOptions<MyResult>,
    private val onMyResultClicked: OnMyResultClicked,
    private val f: (itemCount: Int) -> Unit
) : FirestoreRecyclerAdapter<MyResult, MyResultsAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myResultItemBinding = MyResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ViewHolder(myResultItemBinding)
        myResultItemBinding.onMyResultClicked = onMyResultClicked
//        myResultItemBinding.position = viewHolder.adapterPosition   this is giving wrong positin as -1
        return viewHolder
    }

    override fun onDataChanged() {
        super.onDataChanged()
        f.invoke(itemCount)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: MyResult) {
        holder.myResultItemBinding.myResult = model
        holder.myResultItemBinding.position = position
    }

    class ViewHolder(val myResultItemBinding: MyResultItemBinding) : RecyclerView.ViewHolder(myResultItemBinding.root)

    interface OnMyResultClicked {
        fun onResultDetailClicked(position: Int)
    }
}
