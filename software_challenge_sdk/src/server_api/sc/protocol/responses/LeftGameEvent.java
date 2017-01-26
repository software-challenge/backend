package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value="left")
public class LeftGameEvent
{
	@XStreamAsAttribute
	private String	roomId;

        /**
         * might be needed by XStream
         */
        public LeftGameEvent() {
        }

	public LeftGameEvent(String id)
	{
		this.roomId = id;
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
