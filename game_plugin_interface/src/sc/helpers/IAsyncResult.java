package sc.helpers;

public interface IAsyncResult<T>
{
	public void operate(T result);
}
