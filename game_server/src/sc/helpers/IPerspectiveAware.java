package sc.helpers;

public interface IPerspectiveAware
{
	boolean isVisibleFor(Object viewer, String field);
}
