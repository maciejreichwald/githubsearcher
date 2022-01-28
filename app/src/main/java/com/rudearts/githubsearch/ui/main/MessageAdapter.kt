package com.rudearts.githubsearch.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rudearts.githubsearch.R
import com.rudearts.githubsearch.model.GithubRepository
import java.util.*


internal class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private val data = ArrayList<GithubRepository>()
    private var clickListener: (String) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(MessageViewHolder.LAYOUT, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(get(position), position, clickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun get(position: Int) = data[position]

    fun update(items: List<GithubRepository>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        clickListener = listener
    }
}

internal class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        internal const val LAYOUT = R.layout.item_message
    }

    private val container: View = view.findViewById(R.id.container)
    private val nameText: TextView = view.findViewById(R.id.name)
    private val descriptionText: TextView = view.findViewById(R.id.description)
    private val authorText: TextView = view.findViewById(R.id.author)

    internal fun bind(item: GithubRepository, position: Int, listener: (String) -> Unit) {
        nameText.text = item.name
        authorText.text = "@${item.author}"
        descriptionText.text = item.description

        authorText.isVisible = item.author.isNotEmpty()
        descriptionText.isVisible = item.description.isNotEmpty()

        container.setOnClickListener { listener.invoke(item.url) }
        container.setBackgroundResource(
            when {
                position % 2 == 0 -> R.drawable.item_pressable_grey
                else -> R.drawable.item_pressable_white
            }
        )
    }

}

