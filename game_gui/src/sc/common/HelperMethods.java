package sc.common;

import java.io.File;
import java.io.IOException;

public class HelperMethods {

	/**
	 * Executes a file indicated by <code>filename</code> with the given
	 * <code>parameters</code>.
	 * 
	 * @param filename
	 * @param parameters
	 * @throws IOException
	 * @throws UnsupportedFileExtensionException
	 */
	public static void exec(final String filename, final String[] parameters)
			throws IOException, UnsupportedFileExtensionException {
		final File file = new File(filename);
		final String ext = getFileExtension(file);
		/*
		 * have to parse on my own because exec() split after each white space.
		 * you must not add additional white spaces in the command array. the
		 * processbuilder will add it automatically.
		 */
		String[] command;
		int offset;
		if (ext.equals(".jar")) {
			command = new String[3 + parameters.length];
			command[0] = "java";
			command[1] = "-jar";
			offset = 2;
		} else {
			throw new UnsupportedFileExtensionException();// TODO remove?
		}
		command[offset] = filename;
		for (int i = 0; i < parameters.length; i++) {
			command[i + offset + 1] = parameters[i];
		}

		// only for test purpose
		// for (int i = 0; i < command.length; i++)
		// System.out.print(command[i]);
		// System.out.println();

		Runtime.getRuntime().exec(command);
	}

	public static String getFileExtension(File file) {
		String filename = file.getName();

		int index = filename.lastIndexOf(".");

		return filename.substring(index);
	}
}
