package sc.server.helpers

import sc.networking.INetworkInterface
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class StringNetworkInterface(data: String): INetworkInterface {
    private val outputStream = ByteArrayOutputStream()
    private val inputStream: InputStream = NonEndingByteArrayInputStream(data.toByteArray())
    
    @Throws(IOException::class)
    override fun close() {
    }
    
    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return inputStream
    }
    
    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        return outputStream
    }
    
    @get:Throws(IOException::class)
    val data: String
        get() {
            outputStream.flush()
            return outputStream.toString()
        }
    
    override fun toString(): String = "String@" + this.hashCode()
}