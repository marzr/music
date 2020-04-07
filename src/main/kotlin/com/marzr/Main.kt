package com.marzr

import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.math.min
import kotlin.math.sin

fun main(args: Array<String>) {
    val af = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)
    val line = AudioSystem.getSourceDataLine(af)
    line.open(af, SAMPLE_RATE)
    line.start()

    val file = File(args[0])
    file.readText()
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.toDouble() }
        .forEach {
            play(line, Note(it), 500)
            play(line, Note.REST, 10)
        }

    line.drain()
    line.close()
}


private fun play(line: SourceDataLine, note: Note, ms: Int) {
    val ms1 = min(ms, SECONDS * 1000)
    val length = SAMPLE_RATE * ms1 / 1000
    line.write(note.sin, 0, length)
}

const val SAMPLE_RATE = 16 * 1024 // ~16KHz
const val SECONDS = 2

class Note(f: Double) {

    companion object {
        val REST = Note(0.0)
    }

    val sin = ByteArray(SECONDS * SAMPLE_RATE)

    init {
        for (i in sin.indices) {
            val period = SAMPLE_RATE.toDouble() / f
            val angle = 2.0 * Math.PI * i / period
            sin[i] = (sin(angle) * 127f).toByte()
        }
    }
}