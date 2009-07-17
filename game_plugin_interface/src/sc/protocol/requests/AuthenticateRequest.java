package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("authenticate")
public class AuthenticateRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	password;

	public AuthenticateRequest(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return this.password;
	}
}
