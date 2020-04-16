package sc.server.network

import org.slf4j.LoggerFactory
import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.responses.ProtocolErrorMessage
import sc.server.ServiceManager
import java.io.IOException
import java.util.*

/** The ClientManager serves as a lookup table for all active connections.  */
class ClientManager : Runnable, IClientListener {

    /** List of all XStreamClients */
    val clients = ArrayList<Client>()

    /** Listener waits for new clients to connect  */
    private val clientListener = NewClientListener()

    private var running: Boolean = false
    private var thread: Thread? = null

    private var onClientConnected: ((Client) -> Unit)? = null

    init {
        this.running = false
        this.thread = null
    }

    /**
     * Adds the given `newClient` and notifies all listeners by
     * invoking `onClientConnected`.<br></br>
     * *(only used by tests and addAll())*
     */
    fun add(newClient: Client) {
        this.clients.add(newClient)
        newClient.addClientListener(this)
        onClientConnected?.invoke(newClient)
    }

    fun setOnClientConnected(consumer: (Client) -> Unit) {
        onClientConnected = consumer
    }

    /** Fetch new clients  */
    override fun run() {
        this.running = true

        logger.info("ClientManager running")

        while(this.running && !Thread.interrupted()) {
            try {
                // Waits blocking for new Client
                val client = this.clientListener.fetchNewSingleClient()

                logger.info("Delegating new client to ClientManager...")
                this.add(client)
                logger.info("Delegation done")
            } catch(e: InterruptedException) {
                if(this.running) {
                    logger.warn("Interrupted while waiting for a new client", e)
                } else {
                    logger.warn("Client manager is shutting down")
                }
            }
        }

        this.running = false
        logger.info("ClientManager closed")
    }
    
    /**
     * Starts the ClientManager and ClientListener in it's own daemon thread. This method should be used only once.
     *
     * @see NewClientListener#start()
     */
    @Throws(IOException::class)
    fun start() {
        this.clientListener.start()
        if(this.thread == null) {
            this.thread = ServiceManager.createService(this.javaClass.simpleName, this)
            this.thread!!.start()
        }
    }

    fun close() {
        this.running = false

        if(this.thread != null) {
            this.thread!!.interrupt()
        }

        this.clientListener.close()

        for(i in this.clients.indices) {
            val client = this.clients[i]
            client.stop()
        }
    }

    /**
     * On client disconnect remove it from the list
     *
     * @param source client which disconnected
     */
    override fun onClientDisconnected(source: Client) {
        logger.info("Removing client {} from client manager", source)
        clients.remove(source)
    }

    /**
     * Do nothing on error
     *
     * @param source client, which rose the error
     * @param packet which contains the error
     */
    override fun onError(source: Client, packet: ProtocolErrorMessage) {
        // TODO Error handling needs to happen
    }

    /**
     * Ignore any request
     *
     * @param source client, which send the package
     * @param packet to be handled
     *
     * @throws RescuableClientException never
     */
    @Throws(RescuableClientException::class)
    override fun onRequest(source: Client, packet: PacketCallback) {
        // TODO Handle Request?
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ClientManager::class.java)
    }

}
