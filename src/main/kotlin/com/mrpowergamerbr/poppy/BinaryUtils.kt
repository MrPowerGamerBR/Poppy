package com.mrpowergamerbr.poppy

object BinaryUtils {
    // From kotlinx-io
    fun Int.reverseBytes(): Int {
        return (this and -0x1000000 ushr 24) or
                (this and 0x00ff0000 ushr 8) or
                (this and 0x0000ff00 shl 8) or
                (this and 0x000000ff shl 24)
    }

    fun Long.reverseBytes(): Long {
        return (this and -0x100000000000000L ushr 56) or
                (this and 0x00ff000000000000L ushr 40) or
                (this and 0x0000ff0000000000L ushr 24) or
                (this and 0x000000ff00000000L ushr 8) or
                (this and 0x00000000ff000000L shl 8) or
                (this and 0x0000000000ff0000L shl 24) or
                (this and 0x000000000000ff00L shl 40) or
                (this and 0x00000000000000ffL shl 56)
    }

    fun Short.reverseBytes(): Short {
        val i = toInt() and 0xffff
        val reversed = (i and 0xff00 ushr 8) or
                (i and 0x00ff shl 8)
        return reversed.toShort()
    }
}