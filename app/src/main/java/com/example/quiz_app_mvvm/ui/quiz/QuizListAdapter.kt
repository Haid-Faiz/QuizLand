package com.example.quiz_app_mvvm.ui.quiz

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.SingleListItemBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class QuizListAdapter(
    options: FirestoreRecyclerOptions<QuizModel>,
    private val onQuizListItemClicked: OnQuizListItemClicked,
//        private val f: (itemCount: Int) -> Unit
) : FirestoreRecyclerAdapter<QuizModel, QuizListAdapter.ViewHolder>(options) {


    override fun onDataChanged() {
        super.onDataChanged()
//        f.invoke(itemCount)   // f(getItemCount())  we can also invoke it like this
        onQuizListItemClicked.onListItemChanged(itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleListItemBinding = SingleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ViewHolder(singleListItemBinding)
        singleListItemBinding.onQuizListItemClicked = onQuizListItemClicked
        singleListItemBinding.unenrolMenuButton.setOnClickListener {
            // this popup menu needs to be tied to a view
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.single_list_item_menu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.unenrol_button -> {
                            onQuizListItemClicked.onUnEnrolClicked(viewHolder.adapterPosition)
                            true
                        }
                        else -> false
                    }
                }
            })
            popupMenu.show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuizModel) {
        holder.singleListItemBinding.quiz = model
        holder.singleListItemBinding.position = position
    }

    class ViewHolder(val singleListItemBinding: SingleListItemBinding) : RecyclerView.ViewHolder(singleListItemBinding.root)

    interface OnQuizListItemClicked {
        fun onQuizItemClicked(position: Int)
        fun onUnEnrolClicked(adapterPosition: Int)
        fun onListItemChanged(itemCount: Int)
    }
}