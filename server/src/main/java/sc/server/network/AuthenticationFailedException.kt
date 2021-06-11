package sc.server.network

import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.requests.AdminLobbyRequest

/** Thrown if attempting to authenticate with a wrong password. */
class AuthenticationFailedException:
        RescuableClientException("Failed to authenticate as administrator")

class UnauthenticatedException(packet: AdminLobbyRequest):
        RescuableClientException("Request $packet requires authentication")