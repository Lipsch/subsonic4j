package ch.lipsch.subs4j.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;

import ch.lipsch.subs4j.TestConfig;
import ch.lipsch.subsonic4j.SubsonicFactory;
import ch.lipsch.subsonic4j.SubsonicService;
import ch.lipsch.subsonic4j.model.Artist;
import ch.lipsch.subsonic4j.model.Directory;
import ch.lipsch.subsonic4j.model.Index;
import ch.lipsch.subsonic4j.model.Song;
import ch.lipsch.subsonic4j.tools.FolderTool;

public class Subsonic4PMSLikeTest extends TestCase {

	private SubsonicService subsonicService = null;

	@Override
	@Before
	public void setUp() throws MalformedURLException {
		subsonicService = SubsonicFactory.createService(new URL(
				TestConfig.SUBSONIC_URL), TestConfig.USER1_CREDENTIALS);
	}

	public void testPrintMusicTree() {
		print("#######################", null);
		List<Index> allRootIndexes = FolderTool
				.getAllRootIndexes(subsonicService);
		assertTrue(allRootIndexes.size() > 0);
		for (Index index : allRootIndexes) {
			print("Index: {0}", index.getIdentifier());
			for (Artist artist : index.getArtists()) {
				print("Artist: {0}", artist);
				Directory artistDirectory = subsonicService
						.getMusicDirectory(artist);
				printDirectory(artistDirectory, 0);
			}
		}
	}

	private void printDirectory(Directory directory, int recursionLevel) {
		StringBuilder prefix = new StringBuilder();
		for (int i = 0; i < recursionLevel - 1; i++) {
			prefix.append(" ");
		}

		print(prefix.toString() + "Directory: {0}", directory);

		prefix.append(" ");

		for (Song song : directory.getSongs()) {
			print(prefix + "Song: {0}", song);
		}
		for (Directory subDirectory : directory.getChildDirectories()) {
			printDirectory(subDirectory, recursionLevel + 1);
		}
	}

	private void print(String message, Object... objects) {
		System.out.println(MessageFormat.format(message, objects));
	}
}
