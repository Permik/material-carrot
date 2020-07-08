package xyz.santtu.materialcarrot

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.math.abs

/**
 * Update the display of our current UTC offset i.e. UTC+1 or UTC-1:30
 */
fun UtcOffset(): String {
    val now = Calendar.getInstance()
    var offsetMinutes = (now[Calendar.ZONE_OFFSET] + now[Calendar.DST_OFFSET]) / 60000
    val offsetPrefix = if (offsetMinutes < 0) "-" else "+"
    offsetMinutes = abs(offsetMinutes)
    if (offsetMinutes % 60 == 0) {
        return String.format(
            "UTC%s%d", offsetPrefix,
            offsetMinutes / 60
        )
    } else {
        return String.format(
            "UTC%s%d:%d", offsetPrefix,
            offsetMinutes / 60, offsetMinutes % 60
        )
    }
}

/**
 * Increase readability of String by inserting spaces every 4 characters
 *
 * @param unreadable
 * String that needs formatting
 */
fun formatAddHexReadability(unreadable: String): String {
    var formattedString = ""
    var i = 0
    while (i < unreadable.length) {
        formattedString += unreadable.substring(i, i + 4) + " "
        i += 4
    }
    return formattedString
}

/**
 * Take a String and return a hex
 *
 * @param s
 * String of bytes to be converted
 * @return Simple hex-encoded string, for example: a0e23b
 */
fun md5(s: String): String {
    try {
        val md = MessageDigest.getInstance("MD5")
        md.update(s.toByteArray(), 0, s.length)
        return toHex(md.digest())
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * Convert a byte array to a hex-string. For md5 strings for example
 *
 * @param hashValue
 * Input bytearray
 * @return ascii hexcode representation of input
 */
fun toHex(hashValue: ByteArray): String {
    val hexString = StringBuilder()
    for (i in hashValue.indices) {
        val hex = Integer.toHexString(0xFF and hashValue[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}

private val HEX_CHARS = "0123456789abcdef"

fun String.hexStringToByteArray() : ByteArray {

    val result = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        val firstIndex = HEX_CHARS.indexOf(this[i])
        val secondIndex = HEX_CHARS.indexOf(this[i + 1])

        val octet = firstIndex.shl(4).or(secondIndex)
        result.set(i.shr(1), octet.toByte())
    }

    return result
}

/**
 * Perform all the necessary motp calculations called when the user clicks
 * the Ok button
 */
fun generateOtp(pin: String, secret: String): String {
    val now = Calendar.getInstance()
    var epoch = now.timeInMillis.toString()
    epoch = epoch.substring(0, epoch.length - 4)
    val hash = md5(epoch + secret + pin)
    val otp = hash.substring(0, 6)
    return otp
}