package com.mrpowergamerbr.poppy

enum class OpCode(val op: UByte) {
    PUSH_INT(0x00u),
    POP(0x01u),
    JUMP(0x02u),
    BT(0x03u),
    BF(0x04u),
    ADD(0x05u),
    SUB(0x06u),
    MUL(0x07u),
    DIV(0x08u),
    MOD(0x09u),
    CALL(0x10u),
    STORE(0x11u),
    LOAD(0x12u),
    DUP(0x13u),
    PUSH_CONST(0x14u),
    EQUAL(0x15u),
    ST(0x16u),
    SF(0x17u),
    BYE(0x18u), ;

    companion object {
        fun fromCode(op: UByte): OpCode {
            return entries.first { it.op == op }
        }
    }
}