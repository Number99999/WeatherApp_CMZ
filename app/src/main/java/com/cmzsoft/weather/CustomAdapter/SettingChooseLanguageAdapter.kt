package com.cmzsoft.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.ChooseLanguageModel
import com.cmzsoft.weather.Model.ItemChooseLanguageModel
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.R

class SettingChooseLanguageAdapter(
    val items: MutableList<ItemChooseLanguageModel>, // MutableList to update the list
    private val itemClickListener: OnItemClickListener // Listener for item click
) : RecyclerView.Adapter<SettingChooseLanguageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int) // Listen for item click event
    }

    private var _data: ChooseLanguageModel = LocalStorageManager.getObject<ChooseLanguageModel>(
        KeysStorage.establish, ChooseLanguageModel::class.java
    ) ?: ChooseLanguageModel()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val title: TextView = itemView.findViewById(R.id.title)
        val isChoose: ImageView = itemView.findViewById(R.id.isChoose)

        init {
            // Listen for item click
            itemView.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)  // Call the listener when clicked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_language, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.idIcon)
        holder.title.text = item.title

        // Check if the item is selected, and change the image resource accordingly
        if (item.isChoose) {
            holder.isChoose.setImageResource(R.drawable.option_choose)
        } else {
            holder.isChoose.setImageResource(R.drawable.option_not_choose)
        }
    }

    // Method to update the list and reset selection
    fun updateLanguageSelection(position: Int) {
        // Deselect all items
        for (i in items.indices) {
            items[i].isChoose = false
        }
        // Select the clicked item
        items[position].isChoose = true
        notifyDataSetChanged() // Refresh the RecyclerView
    }
}
