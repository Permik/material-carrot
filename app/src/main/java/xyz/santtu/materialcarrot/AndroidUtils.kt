package xyz.santtu.materialcarrot

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
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