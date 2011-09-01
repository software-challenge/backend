package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("authenticate")
public class AuthenticateRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	passphrase;
        
        /**
         * might be needed by XStream
         */
        public AuthenticateRequest() {
        }

	public AuthenticateRequest(String passphrase)
	{
		this.passphrase = passphrase;
	}

	public String getPassword()
	{
		return this.passphrase;
	}
}
