package kumoh.iat.v2.ui.customadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kumoh.iat.v2.R
import kumoh.iat.v2.databinding.ItemSubContentBinding

class CustomListedAdapter(
    context: Context,
    resource: Int,
    private val questions: List<String>,
    private val cases: List<List<String>>
): ArrayAdapter<String>(context, resource, questions) {

    override fun getCount(): Int = questions.size

    @SuppressLint("ViewHolder")
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View = ItemSubContentBinding.inflate(
        LayoutInflater.from(parent.context)
    ).apply {
        tvQuestion.text = String.format(
            context.getString(R.string.survey_sub_question), pos+1, questions[pos]
        )

        cllSubContent.setAdapter(
            CustomNormalCaseAdapter(
                context = context,
                resource = R.layout.item_normal_case,
                cases = cases[pos]
            )
        )
    }.root
}