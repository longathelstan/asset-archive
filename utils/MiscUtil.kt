package dev.blend.util.misc

import dev.blend.Client
import kotlinx.io.IOException
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.channels.Channels

object MiscUtil {

    @Throws(IOException::class)
    fun getResourceAsByteBuffer(path: String, bufferSize: Int = 1024): ByteBuffer {
        val source = Client::class.java.getResourceAsStream("/assets/blend/$path")
        checkNotNull(source)
        val readableByteChannel = Channels.newChannel(source)
        var buffer = BufferUtils.createByteBuffer(bufferSize)
        while (true) {
            val bytes = readableByteChannel.read(buffer)
            if (bytes == -1) {
                break
            }
            if (buffer.remaining() == 0) {
                buffer = buffer.resizeBuffer(buffer.capacity() * 3 / 2)
            }
        }
        buffer.flip()
        return MemoryUtil.memSlice(buffer)
    }

    private fun ByteBuffer.resizeBuffer(newCapacity: Int): ByteBuffer {
        val newBuffer = BufferUtils.createByteBuffer(newCapacity)
        flip()
        newBuffer.put(this)
        return newBuffer
    }

}