package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("authenticate")
public class AuthenticateRequest implements ILobbyRequest
{
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
