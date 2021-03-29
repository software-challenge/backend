package sc.server.network

import org.slf4j.LoggerFactory
import sc.protocol.responses.ProtocolErrorMessage
import sc.server.ServiceManager
import java.io.Closeable
import java.io.IOException
import java.util.*

/** The ClientManager serves as a lookup table for all active connections.  */
class ClientManager : Runnable, IClientListener, Closeable {

    /** List of all XStreamClients. */
    val clients = ArrayList<Client>()

    /** Listener waits for new clients to connect. */
    private val clientListener = NewClientListener()

    private var running: Boolean = false
    private var serviceThread: Thread? = null

    private var onClientConnected: ((Client) -> Unit)? = null

    init {
        running = false
        serviceThread = null
    }

    /**
     * Adds the given `newClient` and notifies all listeners by invoking `onClientConnected`.
     *
     * *(only used by tests and addAll())*
     */
    fun add(newClient: Client) {
        clients.add(newClient)
        newClient.addClientListener(this)
        onClientConnected?.invoke(newClient)
    }

    fun setOnClientConnected(consumer: (Client) -> Unit) {
        onClientConnected = consumer
    }

    /** Fetch new clients. */
    override fun run() {
        running = true

        logger.info("ClientManager running")

        while(running && !Thread.interrupted()) {
            try {
                // Waits blocking for new Client
                val client = clientListener.fetchNewSingleClient()

                logger.info("Delegating new client to ClientManager...")
                add(client)
                logger.info("Delegation done")
            } catch(e: InterruptedException) {
                if(running)
                    logger.warn("Interrupted while waiting for a new client", e)
            }
        }

        running = false
        logger.info("ClientManager closed")
    }
    
    /**
     * Starts the ClientManager and ClientListener in it's own daemon thread. This method should be used only once.
     *
     * @see NewClientListener.start
     */
    @Throws(IOException::class)
    fun start() {
        clientListener.start()
        if(serviceThread == null)
            serviceThread = ServiceManager.createService(javaClass.simpleName, this).apply { start() }
    }

    override fun close() {
        running = false
        serviceThread?.interrupt()
        clientListener.close()
        while (clients.size > 0) {
            try {
                clients.removeAt(clients.lastIndex).stop()
            } catch (ignored: ArrayIndexOutOfBoundsException) {
                // Client was removed concurrently
            }
        }
    }

    /** Remove disconnected client. */
    override fun onClientDisconnected(source: Client) {
        logger.info("Removing client $source from client manager")
        clients.remove(source)
    }

    /** Do nothing on error. */
    override fun onError(source: Client, packet: ProtocolErrorMessage) {
        // TODO Error handling needs to happen
    }

    /** Ignore any request. */
    override fun onRequest(source: Client, packet: PacketCallback) {
        // TODO Handle Request?
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ClientManager::class.java)
    }

}
