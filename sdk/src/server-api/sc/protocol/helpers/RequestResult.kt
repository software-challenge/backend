package sc.protocol.helpers

import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage

sealed class RequestResult<T: ProtocolMessage> {
    data class Success<T: ProtocolMessage>(val result: T): RequestResult<T>()
    data class Error<T: ProtocolMessage>(val error: ProtocolErrorMessage): RequestResult<T>()
}
