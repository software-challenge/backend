package sc.helpers

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.KXml2Driver
import sc.protocol.helpers.LobbyProtocol

/*
* Using the KXml2 parser because the default (Xpp3) and StAX can't parse some special characters in attribute values:
* <protocol><authenticate passphrase="examplepassword"/>
* <prepare gameType="swc_2018_hase_und_igel">
*   <slot displayName="HÃ¤schenschule" canTimeout="true"/>
*   <slot displayName="Testhase" canTimeout="true"/>
* </prepare>
*/
val xStream by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    XStream(KXml2Driver()).apply {
        setMode(XStream.NO_REFERENCES)
        LobbyProtocol.registerMessages(this)
    }
}
