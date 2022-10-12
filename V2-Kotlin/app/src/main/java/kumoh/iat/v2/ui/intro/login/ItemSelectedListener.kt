package kumoh.iat.v2.ui.intro.login

import android.view.View
import android.widget.AdapterView

class ItemSelectedListener(
    private val block: (Int) -> Unit
): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        block(pos)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}