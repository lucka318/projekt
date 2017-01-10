package hr.fer.diplomski.projekt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import hr.fer.diplomski.projekt.reprezentacije.BOW;
import hr.fer.diplomski.projekt.reprezentacije.StopWords;

public class Main {

	public static void main(String[] args) throws IOException {

		StopWords sw = new StopWords();
		File stop_words = new File(
				"C://Users//Lu//Desktop//Lu//FER//Diplomski//stopwords");
		sw.setDirectory(stop_words.toPath());
		sw.makeStopWords();
		BOW bow = new BOW(sw.getStopWords());
		File dir_wikipedia = new File(
				"C://Users//Lu//Desktop//Lu//FER//Diplomski//text_sources//wikipedia");
		bow.setDirectory(dir_wikipedia.toPath());
		bow.makeDictionary();
		// System.out.println(bow.getDictionary());
		Map<String, Integer> dic = bow.getDictionary();
		Map<String, Vector<Double>> m = bow.getBOWRepresentation();
		Set<String> d = m.keySet();

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("C://Users//Lu//Desktop//Lu//FER//Diplomski//arff_files//arfff.arff"), "utf-8"))) {
			writer.write("//dodaj Relation\n");
			writer.write("\n");
			writer.write("\n");
			Set<String> dic_keys = dic.keySet();
			writer.write("@ATTRIBUTE id STRING\n");
			for (String s : dic_keys) {
				writer.write("@ATTRIBUTE " + s + " NUMERIC\n");
			}
			writer.write("@ATTRIBUTE phenotypeClass {0,1}\n");
			writer.write("\n");
			writer.write("@DATA\n");
			for (String s : d) {
				System.out.println(s);
				writer.write("\"" + s + "\",");
				Vector<Double> v = m.get(s);
				for(int i = 0; i < v.size(); i++) {
					writer.write(v.get(i)+",");
				}
				writer.write("\n");
			}
			
			writer.close();
			//dodaj klasuuu
		}

	}

}
