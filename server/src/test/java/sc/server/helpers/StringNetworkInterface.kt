package sc.server.helpers

import sc.networking.INetworkInterface
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class StringNetworkInterface(data: String): INetworkInterface {
    private val inputStream = NonEndingByteArrayInputStream(data.toByteArray())
    private val outputStream = ByteArrayOutputStream()
    
    override fun getInputStream(): InputStream = inputStream
    override fun getOutputStream(): OutputStream = outputStream
    override fun close() {
        inputStream.close()
        outputStream.close()
    }
    
    fun readData(): String {
        outputStream.flush()
        return outputStream.toString()
    }
}