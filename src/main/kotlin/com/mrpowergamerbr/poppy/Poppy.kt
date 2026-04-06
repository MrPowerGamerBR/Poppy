package com.mrpowergamerbr.poppy

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File
import java.util.*

class PoppyBaseCommand : CliktCommand() {
    override fun run() = Unit
}

class PoppyExecuteCommand : CliktCommand(name = "execute") {
    val filePath by argument()

    override fun run() {
        val file = File(filePath)
        val reader = ByteArrayReader(file.readBytes())

        // Read all chunks
        val chunks = mutableMapOf<String, ByteArray>()

        while (reader.hasRemaining()) {
            val chunkType = reader.readBytes(4).toString(Charsets.US_ASCII)
            val chunkSize = reader.readIntLe()
            println("Chunk: $chunkType ($chunkSize bytes)")
            val chunk = reader.readBytes(chunkSize)
            chunks[chunkType] = chunk
        }

        println("Chunks: ${chunks}")

        // Now parse each section if needed!
        val functionNames = mutableListOf<String>()
        run {
            val funcReader = ByteArrayReader(chunks["FUNC"]!!)
            while (funcReader.hasRemaining()) {
                val string = funcReader.readBytesUntilNull().toString(Charsets.UTF_8)
                functionNames.add(string)
            }
        }

        val strings = mutableListOf<String>()
        run {
            val strgReader = ByteArrayReader(chunks["STRG"]!!)
            while (strgReader.hasRemaining()) {
                val string = strgReader.readBytesUntilNull().toString(Charsets.UTF_8)
                strings.add(string)
            }
        }

        val vm = VM(
            0,
            functionNames.toTypedArray(),
            strings.toTypedArray(),
            chunks["CODE"]!!,
            Stack(),
            Array(64) { null }
        )

        vm.executeLoop()
    }
}

class PoppyAssembleCommand : CliktCommand(name = "assemble") {
    val filePath by argument()

    override fun run() {
        val file = File(filePath)

        val writer = ByteArrayWriter()

        // Poppy uses a IFF-like format, but we use little endian through
        val lines = file.readLines()
            .map { it.substringBefore("//") }
            .filter { it.isNotBlank() }
            .toMutableList()

        run {
            val chunkWriter = ByteArrayWriter()
            val funcMap = lines.removeAt(0).removePrefix("[").removeSuffix("]").split(", ").map { it.trim() }

            for (string in funcMap) {
                chunkWriter.writeBytes(string.toByteArray(Charsets.US_ASCII))
                chunkWriter.writeByte(0x00) // null
            }

            val chunk = chunkWriter.asByteArray()
            writer.writeBytes("FUNC".toByteArray(Charsets.US_ASCII))
            writer.writeIntLe(chunk.size)
            writer.writeBytes(chunk)
        }

        run {
            val chunkWriter = ByteArrayWriter()
            val stringMap = lines.removeAt(0).removePrefix("[").removeSuffix("]").split(", ").map { it.trim() }

            for (string in stringMap) {
                chunkWriter.writeBytes(string.removePrefix("\"").removeSuffix("\"").toByteArray(Charsets.US_ASCII))
                chunkWriter.writeByte(0x00) // null
            }

            val chunk = chunkWriter.asByteArray()
            writer.writeBytes("STRG".toByteArray(Charsets.US_ASCII))
            writer.writeIntLe(chunk.size)
            writer.writeBytes(chunk)
        }

        val codeChunkWriter = ByteArrayWriter()

        while (lines.isNotEmpty()) {
            val line = lines.removeAt(0)
            val opCodeFancyName = line.substringBefore(" ").trim()
            val extra = line.substringAfter(" ").trim()

            val opCode = OpCode.valueOf(opCodeFancyName.uppercase())

            codeChunkWriter.writeIntLe(opCode.op.toInt())

            when (opCode) {
                OpCode.PUSH_INT -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.POP -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.JUMP -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.BT -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.BF -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.ADD -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.SUB -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.MUL -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.DIV -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.MOD -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.CALL -> {
                    val (functionIndexAsString, argCountAsString) = extra.split(", ")

                    val functionIndex = functionIndexAsString.toShort()
                    val argCount = argCountAsString.toShort()

                    codeChunkWriter.writeShortLe(functionIndex)
                    codeChunkWriter.writeShortLe(argCount)
                }
                OpCode.STORE -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.LOAD -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.DUP -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.PUSH_CONST -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.EQUAL -> {
                    codeChunkWriter.writeIntLe(0)
                }
                OpCode.ST -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.SF -> {
                    codeChunkWriter.writeIntLe(extra.toInt())
                }
                OpCode.BYE -> {
                    codeChunkWriter.writeIntLe(0)
                }
            }
        }

        val codeChunk = codeChunkWriter.asByteArray()
        writer.writeBytes("CODE".toByteArray(Charsets.US_ASCII))
        writer.writeIntLe(codeChunk.size)
        writer.writeBytes(codeChunk)

        File(file.nameWithoutExtension + ".pxppy").writeBytes(writer.bytes.toByteArray())
    }
}

fun main(args: Array<String>) = PoppyBaseCommand()
    .subcommands(PoppyExecuteCommand())
    .subcommands(PoppyAssembleCommand())
    .main(args)