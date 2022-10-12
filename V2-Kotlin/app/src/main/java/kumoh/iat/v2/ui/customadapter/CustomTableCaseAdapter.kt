package kumoh.iat.v2.ui.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.ItemTableCaseBinding

class CustomTableCaseAdapter(
    context: Context,
    resource: Int,
    private val cases: List<String>,
    private val selected: Int = -1,
    private val block: (Int) -> Unit
): ArrayAdapter<String>(context, resource, cases) {

    override fun getCount(): Int = cases.size

    @SuppressLint("ViewHolder")
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View = ItemTableCaseBinding.inflate(
        LayoutInflater.from(parent.context)
    ).apply {
        tvNumberCase.text = cases[pos]

        if (cases[pos].length == 1) {
            tvNumberCase.setBackgroundColor(
                if (selected == pos)
                    context.getColor(R.color.primary)
                else
                    context.getColor(R.color.white)
            )

            tvNumberCase.setOnClickListener {
                for (i in 1 until parent.childCount) {
                    parent.getChildAt(i).setBackgroundColor(context.getColor(R.color.white))
                }
                it.setBackgroundColor(context.getColor(R.color.primary))

                block(pos)
            }
        }
    }.root
}