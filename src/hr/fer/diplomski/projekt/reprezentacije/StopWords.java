package hr.fer.diplomski.projekt.reprezentacije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
/**
 * create set with stop words
 * @author Lu
 *
 */
public class StopWords {

	private Path dir;
	private Set<String> stopWords = new HashSet<String>();

	public StopWords() {
	}

	/**
	 * Set directory where all text sources are
	 * 
	 * @param dir
	 */
	public void setDirectory(Path dir) {
		if (dir == null || dir.toFile().isFile()) {
			throw new IllegalArgumentException(
					"Directory is null or is not a directory");
		}
		this.dir = dir;
	}

	public void makeStopWords() throws IOException {
		if (this.dir == null || this.dir.toFile().isFile()) {
			throw new IllegalArgumentException(
					"Path directory where text source files are is not set. Please set directory path with setDirectory method.");
		}

		List<Path> listOfFiles = new ArrayList<Path>();
		try (Stream<Path> paths = Files.walk(this.dir)) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					listOfFiles.add(filePath);
				}
			});
		}

		Pattern pattern = Pattern.compile("[\\w']+");
		for (Path p : listOfFiles) {
			List<String> allLines = Files.readAllLines(p);
			for (String line : allLines) {
				Matcher m = pattern.matcher(line);
				while (m.find()) {
					String word = line.substring(m.start(), m.end()).trim();
					Stemmer stemmer = new Stemmer();
					stemmer.add(word.toCharArray(), word.length());
					stemmer.stem();
					String stemWord = stemmer.toString().trim();
					stopWords.add(stemWord);
				}
			}
		}
	}

	public Set<String> getStopWords() {
		return stopWords;
	}

}
