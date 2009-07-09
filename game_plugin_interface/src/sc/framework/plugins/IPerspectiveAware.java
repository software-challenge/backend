package sc.framework.plugins;

public interface IPerspectiveAware
{
	boolean isVisibleFor(Object viewer, String field);
}
