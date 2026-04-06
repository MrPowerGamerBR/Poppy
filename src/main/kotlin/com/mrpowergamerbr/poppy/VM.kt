package com.mrpowergamerbr.poppy

import com.mrpowergamerbr.poppy.OpCode.*
import com.mrpowergamerbr.poppy.RValue.*
import java.util.Stack

class VM(
    // Instruction Pointer
    var ip: Int,
    val functions: Array<String>,
    val strings: Array<String>,
    val bytecode: ByteArray,
    val stack: Stack<RValue>,
    val locals: Array<RValue?>
) {
    val bytecodeReader = ByteArrayReader(bytecode)

    fun executeLoop() {
        while (ip != bytecodeReader.byteArray.size) {
            bytecodeReader.position = this.ip
            val opCodeRaw = bytecodeReader.readIntLe()
            val extra = bytecodeReader.readIntLe()
            val op = OpCode.fromCode(opCodeRaw.toUByte())

            // println("Executing opcode ($op) $opCodeRaw with extra $extra [stack=$stack]")

            this.ip += 8

            when (op) {
                PUSH_INT -> {
                    this.stack.push(IntRValue(extra))
                }
                POP -> {
                    if (this.stack.isEmpty())
                        error("Trying to pop from an empty stack!")

                    this.stack.pop()
                }
                JUMP -> {
                    this.ip = extra
                }
                BT -> {
                    if (this.stack.isEmpty())
                        error("Trying to branch from an empty stack!")

                    val value = this.stack.pop().toIntRValue()
                    if (value.value == 1)
                        this.ip = extra
                }
                BF -> {
                    if (this.stack.isEmpty())
                        error("Trying to branch from an empty stack!")

                    val value = this.stack.pop().toIntRValue()
                    if (value.value == 0)
                        this.ip = extra
                }
                ADD -> {
                    val a = this.stack.pop().toIntRValue()
                    val b = this.stack.pop().toIntRValue()

                    this.stack.push(IntRValue(b.value + a.value))
                }
                SUB -> {
                    val a = this.stack.pop().toIntRValue()
                    val b = this.stack.pop().toIntRValue()

                    this.stack.push(IntRValue(b.value - a.value))
                }
                MUL -> {
                    val a = this.stack.pop().toIntRValue()
                    val b = this.stack.pop().toIntRValue()

                    this.stack.push(IntRValue(b.value * a.value))
                }
                DIV -> {
                    val a = this.stack.pop().toIntRValue()
                    val b = this.stack.pop().toIntRValue()

                    this.stack.push(IntRValue(b.value / a.value))
                }
                MOD -> {
                    val a = this.stack.pop().toIntRValue()
                    val b = this.stack.pop().toIntRValue()

                    this.stack.push(IntRValue(b.value % a.value))
                }
                CALL -> {
                    val functionIndex = (extra and 0xFFFF).toShort()
                    val argCount = (extra ushr 16).toShort()

                    val functionName = this.functions[functionIndex.toInt()]

                    when (functionName) {
                        "println" -> {
                            val args = Array(argCount.toInt()) { this.stack.pop() }
                            println(args.joinToString("") { it.asString() })
                        }
                        "print" -> {
                            val args = Array(argCount.toInt()) { this.stack.pop() }
                            print(args.joinToString("") { it.asString() })
                        }
                        else -> error("Unknown function \"$functionName\"")
                    }
                }
                STORE -> {
                    val value = this.stack.pop()
                    this.locals[extra] = value
                }
                LOAD -> {
                    this.stack.push(this.locals[extra])
                }
                DUP -> {
                    // The extra is the count, 0 = acts like pop, 1 = just duplicates once (so essentially does not do anything), 2 = duplicates once, etc
                    val value = this.stack.pop()
                    repeat(extra) {
                        this.stack.push(value)
                    }
                }
                PUSH_CONST -> {
                    val value = this.strings[extra]
                    this.stack.push(StringRValue(value))
                }
                EQUAL -> {
                    val value = this.stack.pop()
                    val other = this.stack.pop()
                    this.stack.push(if (value == other) BooleanRValue(true) else BooleanRValue(false))
                }
                ST -> {
                    if (this.stack.isEmpty())
                        error("Trying to branch from an empty stack!")

                    val value = this.stack.pop().toIntRValue()
                    if (value.value == 1)
                        this.ip += extra
                }
                SF -> {
                    if (this.stack.isEmpty())
                        error("Trying to branch from an empty stack!")

                    val value = this.stack.pop().toIntRValue()
                    if (value.value == 0)
                        this.ip += extra
                }
                BYE -> {
                    this.ip = bytecodeReader.byteArray.size
                }
            }
        }
    }
}