package sc.shared

import com.thoughtworks.xstream.XStream

fun getXStream() =
        XStream().apply {
            setMode(XStream.NO_REFERENCES)
            autodetectAnnotations(true)
        }
