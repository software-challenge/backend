package sc.helpers;

public interface Function<FROM, TO>
{
	TO operate(FROM val);
}
