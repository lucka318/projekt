package hr.fer.diplomski.projekt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrviZadatak {

	public static void main(String[] args) throws IOException {

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
				"C://Users//Lu//Desktop//Lu//FER//Diplomski//biochemical_labels.txt");
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
					pos.add(vr[1]);
				}
				if (vr[i].equals("0")) {
					neg.add(vr[1]);
				}
			}
			Fenotip f = new Fenotip(fenotipovi[i], pos, neg);
			fenotipBakterije.add(f);
		}
		// System.out.println(bakterije);
		
		System.out.println(fenotipBakterije);

	}
}
