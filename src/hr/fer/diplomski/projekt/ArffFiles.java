package hr.fer.diplomski.projekt;

import hr.fer.diplomski.projekt.reprezentacije.BOW;
import hr.fer.diplomski.projekt.reprezentacije.StopWords;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ArffFiles {

	public static void main(String[] args) throws IOException {

		/*
		 * 
		 * ======================================================================
		 * ================= extract fenotypes
		 * ==================================
		 * ====================================================
		 */

		File folder = new File(
				"C://Users//Lu//Desktop//Lu//FER//Diplomski//text_sources//wikipedia");
		File[] listOfFiles = folder.listFiles();

		// ucitavanje id-ijeva bakterija i fileova
		List<Bacteria> bakterije = new ArrayList<Bacteria>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String[] num = listOfFiles[i].getName().split("\\(");
				String id = num[1].split("\\)")[0];
				bakterije.add(new Bacteria("", id, listOfFiles[i].getName()));
			}
		}

		// imamo bakterije i fileove za bakterije - dal da drzim u memoriji
		// cijeli tekst ili naziv file-a?

		File labele = new File(
				"C://Users//Lu//Desktop//Lu//FER//Diplomski//ncbiBacmap_labels.txt");
		List<String> lines = Files.readAllLines(labele.toPath());
		String[] fenotipovi = lines.get(0).split("\\t");

		List<String[]> matricaBakterija = new ArrayList<String[]>();
		for (int j = 1; j < lines.size(); j++) {
			String[] vrijednosti = lines.get(j).split("\\t");
			matricaBakterija.add(vrijednosti);
		}
		List<Fenotip> fenotipBakterije = new ArrayList<Fenotip>();
		for (int i = 2; i < fenotipovi.length; i++) {
			List<String> pos = new ArrayList<String>();
			List<String> neg = new ArrayList<String>();
			for (int j = 0; j < matricaBakterija.size(); j++) {
				String[] vr = matricaBakterija.get(j);
				if (vr[i].equals("1")) {
					pos.add(vr[0]); // dodajemo id???
				}
				if (vr[i].equals("0")) {
					neg.add(vr[0]);
				}
			}
			Fenotip f = new Fenotip(fenotipovi[i], pos, neg);
			fenotipBakterije.add(f);
		}
		// u fenotipBakterije imamo SVE fenotipove iz jednog file-a
		// za svaki fenotip imamo listu bakterija koje su pozitivno ocjenjene i
		// negativno ocjenjene

		// TODO - pretociti bakterije u BOW i stvoriti ARFF file za svaki
		// fenotip

		/*
		 * Prvo cemo za svaku bakteriju iz corpusa naciniti BOW reprezentaciju
		 * koja ce biti spremljena u mapu tako da mozemo povezivati fenotip sa
		 * bakterijom
		 * ============================================================
		 * =====================
		 */

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

		for (Fenotip f : fenotipBakterije) {
			String fenotipName = f.name;
			List<String> positive = f.positive;
			List<String> negative = f.negative;

			String nameFile = dir_wikipedia.getName() + "_" + labele.getName()
					+ "_" + fenotipName + ".arff";
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(
							"C://Users//Lu//Desktop//Lu//FER//Diplomski//arff_files//"
									+ nameFile), "utf-8"))) {
				writer.write("%Phenotype: " + fenotipName + "\n");
				writer.write("\n");
				writer.write("@RELATION " + fenotipName + "\n");
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
					if (positive.contains(s)) {
						writer.write("\"" + s + "\",");
						Vector<Double> v = m.get(s);
						for (int i = 0; i < v.size(); i++) {
							writer.write(v.get(i) + ",");
						}
						writer.write("1");
						writer.write("\n");
					} else if (negative.contains(s)) {
						writer.write("\"" + s + "\",");
						Vector<Double> v = m.get(s);
						for (int i = 0; i < v.size(); i++) {
							writer.write(v.get(i) + ",");
						}
						writer.write("0");
						writer.write("\n");
					}
				}
				writer.close();
				// dodaj klasuuu
			}

		}

	}
}
