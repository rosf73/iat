package kumoh.iat.v2.util

import android.view.View

val Boolean.visibility: Int
    get() = if (this) View.VISIBLE else View.GONE

fun checkNumberRegex(num: String): Boolean
    = num.isNotBlank()
    && num.isNotEmpty()
    && num.matches(Regex("^(010|011|018)[0-9]{7,8}$"))