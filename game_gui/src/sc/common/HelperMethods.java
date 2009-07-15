package sc.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.StringHelper;

public class HelperMethods {

	private static final Logger logger = LoggerFactory.getLogger(HelperMethods.class);

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
		List<String> commandFragments = new LinkedList<String>();

		if (ext.equals(".jar")) {
			commandFragments.add("java");
			commandFragments.add("-jar");
		} else {
			throw new UnsupportedFileExtensionException();// TODO remove?
		}
		commandFragments.add(filename);
		commandFragments.addAll(Arrays.asList(parameters));

		logger.debug("Executing {}", StringHelper.join(commandFragments, " "));
		ProcessBuilder builder = new ProcessBuilder(commandFragments);
		builder.redirectErrorStream();
		final Process proc = builder.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(proc
						.getInputStream()));
				String line = "";
				try {
					while ((line = input.readLine()) != null) {
						System.out.println(line);
					}
				} catch (IOException e) {
					System.err.println("Failed to redirect STDOUT.");
				}

				System.out.println("Process exited with ExitCode=" + proc.exitValue());
			}
		}).start();
	}

	/**
	 * Returns the file extension of the given <code>file</code>. Otherwise, it
	 * returns the filename of the given <code>file</code>.
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileExtension(File file) {
		String filename = file.getName();

		int index = filename.lastIndexOf('.');
		if (-1 != index) {
			return filename.substring(index);
		}

		return filename;
	}

	/**
	 * Returns the filename without the file extension if present.
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFilenameWithoutFileExt(String filename) {
		int dotindex = filename.lastIndexOf('.');
		if (dotindex != -1) {
			return filename.substring(0, dotindex);
		} else {
			return filename;
		}
	}
}
