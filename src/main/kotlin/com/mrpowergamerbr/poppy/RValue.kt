package com.mrpowergamerbr.poppy

sealed class RValue {
    abstract fun toIntRValue(): IntRValue
    abstract fun asString(): String
}

data class IntRValue(val value: Int) : RValue() {
    override fun toIntRValue(): IntRValue {
        return this
    }

    override fun asString() = value.toString()
}

data class StringRValue(val value: String) : RValue() {
    override fun toIntRValue(): IntRValue {
        return IntRValue(value.toInt())
    }

    override fun asString() = value
}

data class BooleanRValue(val value: Boolean) : RValue() {
    override fun toIntRValue(): IntRValue {
        return IntRValue(if (value) 1 else 0)
    }

    override fun asString() = value.toString()
}