package sc.networking

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.KXml2Driver
import sc.protocol.LobbyProtocol
import java.util.ServiceLoader

interface XStreamProvider {
    companion object {
        /*
        * Using the KXml2 parser because the default (Xpp3) and StAX can't parse some special characters in attribute values:
        * <protocol><authenticate passphrase="examplepassword"/>
        * <prepare gameType="swc_2018_hase_und_igel">
        *   <slot displayName="HÃ¤schenschule" canTimeout="true"/>
        *   <slot displayName="Testhase" canTimeout="true"/>
        * </prepare>
        */
        /** @return a XStream instance with the default project settings. */
        fun pure(): XStream =
                XStream(KXml2Driver()).apply {
                    setMode(XStream.NO_REFERENCES)
                }
    
        /** @return a XStream instance able to handle all messages in the sdk. */
        fun basic(): XStream =
                pure().also { xStream ->
                    LobbyProtocol.registerMessages(xStream)
                }
        
        /** Searches for implementations of [XStreamProvider] using [ServiceLoader]
         * and loads their classes into a new basic XStream instance.
         *
         * @return a XStream instance able to handle custom registered classes. */
        @JvmStatic
        fun currentPlugin(): XStream =
                basic().also { xStream ->
                    ServiceLoader.load(XStreamProvider::class.java).sortedBy { it.javaClass.name }.last().load(xStream)
                }
        
        fun allPlugins(): XStream =
            basic().also { xStream ->
                ServiceLoader.load(XStreamProvider::class.java).forEach { provider -> provider.load(xStream) }
            }
        
        private fun XStreamProvider.load(xStream: XStream) {
            LobbyProtocol.registerAdditionalMessages(xStream, this.classesToRegister)
            this.setup(xStream)
        }
    }
    
    val classesToRegister: Collection<Class<*>>
    
    fun setup(xStream: XStream) {}
    
}