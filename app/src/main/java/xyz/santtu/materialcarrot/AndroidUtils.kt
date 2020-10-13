package xyz.santtu.materialcarrot

import android.text.InputFilter
import android.text.Spanned
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
            for (i in start until end) {
                if (!source.elementAt(i).isHex()) {
                    return ""
                }
            }
        }
        return null
    }

}