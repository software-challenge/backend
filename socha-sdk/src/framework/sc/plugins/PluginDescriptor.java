package sc.plugins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginDescriptor
{
	String name();
	String author() default "Anonymous";
	String uuid();
}
