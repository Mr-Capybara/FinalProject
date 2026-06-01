package com.example.finalproject.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.random.Random

class BrownNoisePlayer {
    @Volatile
    private var running = false
    @Volatile
    private var stopping = false
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
        stopping = false
        audioTrack.play()
        worker = thread(name = "ClarityBrownNoise") {
            val data = ShortArray(bufferSize / 2)
            val fadeInFrames = sampleRate / 2
            val fadeOutFrames = sampleRate / 4
            var frame = 0L
            var stopFrame = 0
            var low = 0.0
            var lower = 0.0
            var drift = 0.0
            while (running) {
                for (i in data.indices) {
                    val white = Random.nextDouble(-1.0, 1.0)
                    low += 0.018 * (white - low)
                    lower += 0.006 * (low - lower)
                    drift += 0.0002 * (Random.nextDouble(-1.0, 1.0) - drift)

                    val fadeIn = (frame.toDouble() / fadeInFrames).coerceIn(0.0, 1.0)
                    val fadeOut = if (stopping) {
                        stopFrame += 1
                        (1.0 - stopFrame.toDouble() / fadeOutFrames).coerceIn(0.0, 1.0)
                    } else {
                        1.0
                    }
                    val sample = ((lower * 0.82) + (low * 0.18) + (drift * 0.08)) * 4_200 * fadeIn * fadeOut
                    data[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    frame += 1
                    if (stopping && stopFrame >= fadeOutFrames) running = false
                }
                audioTrack.write(data, 0, data.size)
            }
        }
    }

    fun stop() {
        if (!running && worker == null) return
        stopping = true
        worker?.join(250)
        running = false
        stopping = false
        worker = null
        track?.run {
            runCatching { pause() }
            runCatching { flush() }
            runCatching { release() }
        }
        track = null
    }
}
