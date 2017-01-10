package hr.fer.diplomski.projekt.reprezentacije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class BOW {

	private Path dir;
	private Map<String, Integer> dictionary = new HashMap<String, Integer>();
	private int lowerThreshold = 100;
	private int upperThreshold = 1000;
	private Set<String> stopWords;

	public BOW(Set<String> stopWords) {
		this.stopWords = stopWords;
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

	public void makeDictionary() throws IOException {
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
					if (stopWords.contains(stemWord)) {
						continue;
					}
					if (dictionary.containsKey(stemWord)) {
						Integer i = dictionary.get(stemWord);
						i++;
						dictionary.put(stemWord, i);
					} else {
						dictionary.put(stemWord, 1);
					}
				}
			}
		}

		Set<String> keys = new HashSet<String>(dictionary.keySet());
		for (String s : keys) {
			if (dictionary.get(s) < lowerThreshold
					|| dictionary.get(s) > upperThreshold || s.length() < 4) {
				dictionary.remove(s);
			}
		}
	}

	public Map<String, Integer> getDictionary() {
		return dictionary;
	}

	public Map<String, Vector<Double>> getBOWRepresentation()
			throws IOException {

		List<Path> listOfFiles = new ArrayList<Path>();
		try (Stream<Path> paths = Files.walk(this.dir)) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					listOfFiles.add(filePath);
				}
			});
		}

		Map<String, Integer> dic = dictionary; // dictionary sa svim relevantnim
												// rijecima
		Map<String, Vector<Double>> bow_rep = new HashMap<String, Vector<Double>>();
		// u koliko se dokumenata koja rijec iz dictionarija pojavljuje
		Map<String, Integer> word_all_doc = new HashMap<String, Integer>();

		Pattern pattern = Pattern.compile("[\\w']+");
		Set<String> keys = dic.keySet();
		for (String s : keys) {
			word_all_doc.put(s, 0);
			for (Path p : listOfFiles) {
				List<String> allLines = Files.readAllLines(p);
				boolean flag = false;
				for (String line : allLines) {
					Matcher m = pattern.matcher(line);
					while (m.find()) {
						String word = line.substring(m.start(), m.end()).trim();
						Stemmer stemmer = new Stemmer();
						stemmer.add(word.toCharArray(), word.length());
						stemmer.stem();
						String stemWord = stemmer.toString().trim();
						if (s.equals(stemWord)) {
							flag = true;
							word_all_doc.put(s, word_all_doc.get(s) + 1);
							break;
						}
					}
					if (flag) {
						break;
					}
				}
			}
		}

		for (Path p : listOfFiles) {
			Map<String, Integer> word_doc = new HashMap<String, Integer>();
			int num_words = 0;
			for (String s : keys) {
				List<String> allLines = Files.readAllLines(p);
				if (!word_doc.containsKey(s)) {
					word_doc.put(s, 0);
				}
				for (String line : allLines) {
					Matcher m = pattern.matcher(line);
					while (m.find()) {
						String word = line.substring(m.start(), m.end()).trim();
						Stemmer stemmer = new Stemmer();
						stemmer.add(word.toCharArray(), word.length());
						stemmer.stem();
						String stemWord = stemmer.toString().trim();
						if (stemWord.equals(s)) {
							num_words++;
							if (word_doc.containsKey(s)) {
								word_doc.put(s, word_doc.get(s) + 1);
							}
						}
					}
				}
			}
			Vector<Double> bow_representation = new Vector<Double>(dic.keySet()
					.size());
			for (String s : dic.keySet()) {
				double value_tf = word_doc.get(s) / (double) num_words;
				double value_idf = 1 + Math.log((double) listOfFiles.size()
						/ (double) word_all_doc.get(s));
				bow_representation.add(value_tf * value_idf);
			}
			String fileName = p.getFileName().toString();
			String[] split = fileName.split("\\(");
			String id = split[1].split("\\)")[0];
			bow_rep.put(id, bow_representation);
		}

		return bow_rep;

	}
}
