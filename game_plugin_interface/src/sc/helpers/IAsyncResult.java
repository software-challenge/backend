package sc.helpers;

public interface IAsyncResult<Input>
{
	public void operate(Input input);
}
