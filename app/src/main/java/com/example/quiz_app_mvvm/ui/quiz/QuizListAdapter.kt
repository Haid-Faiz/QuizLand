package com.example.quiz_app_mvvm.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.SingleListItemBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class QuizListAdapter(
    options: FirestoreRecyclerOptions<QuizModel>,
    private val onQuizListItemClicked: OnQuizListItemClicked,
) : FirestoreRecyclerAdapter<QuizModel, QuizListAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        SingleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun getListSize(): Int = itemCount

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuizModel) {
        holder.bind(model, position)
    }

    inner class ViewHolder(
        val binding: SingleListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: QuizModel, position: Int) = binding.apply {
            listName.text = quiz.name
            if (quiz.imageUrl.isNullOrEmpty()) {
                listImage.isGone = true
            } else {
                listImage.isGone = false
                listImage.load(quiz.imageUrl) {
                    placeholder(R.drawable.placeholder_image)
                }
            }
            listDescription.text = quiz.description
            quizCreatorName.text = quiz.createdBy
            listButton.setOnClickListener {
                onQuizListItemClicked.onQuizItemClicked(position = position)
            }
            unenrolMenuButton.setOnClickListener {
                // This popup menu needs to be tied to a view
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.inflate(R.menu.single_list_item_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.unenrol_button -> {
                            onQuizListItemClicked.onUnEnrolClicked(position)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    interface OnQuizListItemClicked {
        fun onQuizItemClicked(position: Int)
        fun onUnEnrolClicked(adapterPosition: Int)
    }
}
