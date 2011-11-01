package sc.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ManifestHelper {
	private static final Logger logger = LoggerFactory
			.getLogger(ManifestHelper.class);

	public static final String MODULE_VERSION_ATTRIBUTE_KEY = "SC-Module-Version";

	public static final String MANIFEST_LOCATION = "META-INF/MANIFEST.MF";
	
	public static final String MODIFIED_IDENTIFIER = "M";
	
	public static final String MODFIED_READABLE_IDENTIFIER = " (modified)";

	private ManifestHelper() {
		// hide constructor
	}

	private static String cleanModuleVersion(String rawModuleVersion) {
		if (rawModuleVersion == null) {
			return null;
		}

		String cleanModuleVersion = rawModuleVersion;

		if (rawModuleVersion.endsWith(MODIFIED_IDENTIFIER)) {
			cleanModuleVersion = rawModuleVersion.substring(0, rawModuleVersion
					.length() - MODIFIED_IDENTIFIER.length());
			cleanModuleVersion += MODFIED_READABLE_IDENTIFIER;
		}

		return cleanModuleVersion;
	}

	public static String getModuleVersion(Class<?> clazz) {
		Attributes attrs = getAttributes(clazz);
		return cleanModuleVersion(attrs.getValue(MODULE_VERSION_ATTRIBUTE_KEY));
	}

	public static String getModuleVersion(JarFile jar) {
		String moduleVersion = null;

		try {
			Attributes attrs = getAttributesFromManifest(jar.getManifest());
			moduleVersion = attrs.getValue(MODULE_VERSION_ATTRIBUTE_KEY);
		} catch (IOException e) {
			logger.warn("Couldn't load Attributes from Manifest.");
		}

		return cleanModuleVersion(moduleVersion);
	}

	public static Attributes getAttributes(Class<?> clazz) {
		URL url = clazz.getClassLoader().getResource(MANIFEST_LOCATION);
		logger.info("Loading Manifest-Attributes from {}", url);

		InputStream stream = clazz.getClassLoader().getResourceAsStream(
				MANIFEST_LOCATION);
		if (stream != null) {
			try {
				final Manifest manifest = new Manifest(stream);
				stream.close();
				Attributes attrs = getAttributesFromManifest(manifest, clazz);
				logger.info("Read {} attributes from Manifest.", attrs.size());

				return attrs;
			} catch (IOException e) {
				logger.warn("Failed to read Manifest", e);
			}
		} else {
			logger.warn("Couldn't load Manifest. Not a JAR?");
		}

		// Use empty manifest attributes.
		return new Attributes();
	}

	public static Attributes getAttributesFromManifest(final Manifest manifest) {
		return manifest.getMainAttributes();
	}

	public static Attributes getAttributesFromManifest(final Manifest manifest,
			Class<?> clazz) {

		String name = clazz.getName().replace('.', '/');
		int index;
		while ((index = name.lastIndexOf('/')) >= 0) {
			final Attributes attributes = manifest.getAttributes(name
					.substring(0, index + 1));
			if (attributes != null)
				return attributes;
			name = name.substring(0, index);
		}

		return getAttributesFromManifest(manifest);
	}

	public static String getModuleVersion(URI jarUri) {
		logger.info("Loading Manifest-Attributes from {}", jarUri);

		try {
			JarFile jar = new JarFile(new File(jarUri));
			return getModuleVersion(jar);
		} catch (IOException e) {
			logger.warn("Couldn't open JAR to determine MANIFEST content.", e);
		}

		return null;
	}
}
