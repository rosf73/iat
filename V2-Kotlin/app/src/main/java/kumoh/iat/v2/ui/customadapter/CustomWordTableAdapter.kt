package kumoh.iat.v2.ui.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kumoh.iat.v2.databinding.ItemWordRowBinding

class CustomWordTableAdapter(
    context: Context,
    resource: Int,
    private val categories: List<String>,
    private val wordsList: List<String>
): ArrayAdapter<String>(context, resource, categories) {

    override fun getCount(): Int = categories.size

    @SuppressLint("ViewHolder")
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View = ItemWordRowBinding.inflate(
        LayoutInflater.from(parent.context)
    ).apply {
        tvCategory.text = categories[pos]
        tvWords.text = wordsList[pos]
    }.root
}