package kumoh.iat.v2.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.TableLayout

class CustomTableView(
    context: Context,
    attrs: AttributeSet?
) : TableLayout(context, attrs) {

    fun setAdapter(adapter: ArrayAdapter<*>?) {
        if (adapter == null) return

        for (i in 0 until adapter.count) {
            addView(adapter.getView(i, null, this))
        }
    }
}
