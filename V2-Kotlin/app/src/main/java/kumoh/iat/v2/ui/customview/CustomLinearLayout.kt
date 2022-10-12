package kumoh.iat.v2.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout

class CustomLinearLayout(
    context: Context,
    attrs: AttributeSet?
) : LinearLayout(context, attrs) {

    fun setAdapter(adapter: ArrayAdapter<*>?) {
        if (adapter == null) return

        val parent = getChildAt(0) as ViewGroup
        parent.removeAllViews()

        for (i in 0 until adapter.count) {
            parent.addView(adapter.getView(i, null, parent))
        }
    }
}
