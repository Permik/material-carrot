package xyz.santtu.materialcarrot

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import xyz.santtu.materialcarrotutils.isHex

class HexInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source != null){
            var keepOriginal = true;
            var stringBuilder = StringBuilder(end-start)

            for (i in start until end) {
                if (source.length == 1 && !source.elementAt(i).isHex()){
                    return ""
                }
                if (source.elementAt(i).isHex()){
                    stringBuilder.append(source.elementAt(i))
                }else{
                    keepOriginal = false
                }
            }
            if (keepOriginal){
                return null
            }else{
                if (source is Spanned){
                    val spannableString = SpannableString(stringBuilder)
                    TextUtils.copySpansFrom(source, start, end, null, spannableString, 0)
                    return spannableString
                }
            }
        }
        return null
    }

}

/**
 * Copy the current one-time-password to the clipboard. This is a callback
 * for onclick on the password TextView.
 *
 * @param view
 */
fun copyToClipboard(view: TextView?, context: Activity, message: String = "Copied to clipboard") { // Gets a handle to the clipboard service.
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val otpClip = ClipData.newPlainText("text", view?.text.toString())
    cm?.primaryClip?.addItem(otpClip.getItemAt(0))
    Toast.makeText(
        context, message,
        Toast.LENGTH_SHORT
    ).show()
}