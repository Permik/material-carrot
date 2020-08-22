package xyz.santtu.materialcarrot

import org.junit.Test

class UtilUnitTest {
    @Test
    fun toHexValidator_AllValues_ReturnsTrue() {
        // it seems that hex can be calculated with normal ints by adding 34 to it every time
        //val lookupTable = listOf<Int>(1,35,69,103,137,171,205,239)
        val test = ByteArray(8) { x: Int -> (1+(x*34)).toByte() }
        assert(toHex(test) == "0123456789abcdef")
    }

    @Test
    fun hexStringToByteArray_AllValues_ReturnsTrue() {
        val compareTo = ByteArray(8) { x: Int -> (1+(x*34)).toByte() }
        assert("0123456789abcdef".hexStringToByteArray().contentEquals(compareTo))
    }

    // b56e aaec a79f 9518
}