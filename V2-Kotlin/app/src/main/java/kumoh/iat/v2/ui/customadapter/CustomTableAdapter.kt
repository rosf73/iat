package kumoh.iat.v2.ui.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TableRow
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.ItemTableRowBinding

class CustomTableAdapter(
    context: Context,
    resource: Int,
    private val questions: List<String>,
    private val caseCount: Int
): ArrayAdapter<String>(context, resource, questions) {

    override fun getCount(): Int = questions.size

    @SuppressLint("ViewHolder")
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View = ItemTableRowBinding.inflate(
        LayoutInflater.from(parent.context)
    ).apply {
        ctrRow.setBackgroundColor(
            if (pos % 2 == 0)
                context.getColor(R.color.table_row_dark)
            else
                context.getColor(R.color.white)
        )
        tvQuestion.text = questions[pos]

        ctrRow.setAdapter(
            CustomTableCaseAdapter(
                context = context,
                resource = R.layout.item_table_case,
                cases = (1 ..caseCount).toList().map { it.toString() },
                block = {}
            )
        )

        for (i in 1 until ctrRow.childCount) {
            ctrRow.getChildAt(i).layoutParams = TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
            )
        }
    }.root
}