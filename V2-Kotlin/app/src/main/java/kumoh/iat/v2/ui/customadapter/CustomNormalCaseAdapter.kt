package kumoh.iat.v2.ui.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.ItemNormalCaseBinding
import kumoh.iat.v2.util.visibility

class CustomNormalCaseAdapter(
    context: Context,
    resource: Int,
    private val cases: List<String>,
    private val isAssay: List<Int> = emptyList(),
    private val selected: Int = -1
): ArrayAdapter<String>(context, resource, cases) {

    override fun getCount(): Int = cases.size

    @SuppressLint("ViewHolder")
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View = ItemNormalCaseBinding.inflate(
        LayoutInflater.from(parent.context)
    ).apply {
        tvCase.text = cases[pos]
        if (isAssay.isNotEmpty() && isAssay[pos] != 0) {
            etCase.visibility = true.visibility
        }

        if (selected == pos + 1) {
            ivCheckbox.setImageResource(R.drawable.ic_check)
            tvCase.setTextColor(context.getColor(R.color.case_selected))
            etCase.isEnabled = true
        } else {
            ivCheckbox.setImageResource(R.drawable.ic_uncheck)
            tvCase.setTextColor(context.getColor(R.color.case_unselected))
        }

        root.setOnClickListener {
            for (child in parent.children) {
                val container = child as ViewGroup
                (container.getChildAt(0) as ImageView).setImageResource(R.drawable.ic_uncheck)
                (container.getChildAt(1) as TextView).setTextColor(context.getColor(R.color.case_unselected))
                (container.getChildAt(2) as EditText).isEnabled = false
            }

            ivCheckbox.setImageResource(R.drawable.ic_check)
            tvCase.setTextColor(context.getColor(R.color.case_selected))
            etCase.isEnabled = true
        }
    }.root
}