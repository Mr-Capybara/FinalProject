package com.example.finalproject.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.random.Random

class BrownNoisePlayer {
    @Volatile
    private var running = false
    private var track: AudioTrack? = null
    private var worker: Thread? = null

    fun start() {
        if (running) return
        val sampleRate = 44_100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        ).coerceAtLeast(4_096)
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        track = audioTrack
        running = true
        audioTrack.play()
        worker = thread(name = "ClarityBrownNoise") {
            val data = ShortArray(bufferSize / 2)
            var last = 0.0
            while (running) {
                for (i in data.indices) {
                    val white = Random.nextDouble(-1.0, 1.0)
                    last = (last + 0.02 * white) / 1.02
                    data[i] = (last * 14_000).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                audioTrack.write(data, 0, data.size)
            }
        }
    }

    fun stop() {
        running = false
        worker?.join(250)
        worker = null
        track?.run {
            runCatching { pause() }
            runCatching { flush() }
            runCatching { release() }
        }
        track = null
    }
}
