package pl.expensive.tag

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_tag_item.view.*
import pl.expensive.R
import pl.expensive.inflateLayout
import pl.expensive.storage.Tag
import kotlin.properties.Delegates

class TagViewHolder(itemView: View, private val tagSelected: (Tag, Boolean) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun update(tag: Tag) {
        itemView.vTagName.text = tag.name
        itemView.setOnClickListener { tagSelected(tag, true) }
    }
}

class TagsAdapter(private val tagSelected: (Tag, Boolean) -> Unit) : RecyclerView.Adapter<TagViewHolder>() {
    var data: MutableList<Tag> by Delegates.vetoable(mutableListOf()) { p, old, new -> old != new }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder =
            TagViewHolder(parent.inflateLayout(R.layout.view_tag_item), tagSelected)

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) =
            holder.update(data[position])

    override fun getItemCount(): Int = data.size

    fun addFront(tag: Tag) {
        data.add(0, tag)
        notifyItemInserted(0)
    }

    fun replace(tags: List<Tag>) {
        if (tags == data) {
            return
        }
        data.clear()
        data.addAll(tags)
        notifyDataSetChanged()
    }
}
