package freischaltungstool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Language {

	private static final String PATH = "Freischaltungstool-Config\\languages.txt";

	/**
	 * 
	 * 
	 * 
	 * @param empty
	 *            ArrayListe<ArrayList<String>>
	 * 
	 * @return boolean
	 * 
	 */
	public boolean getTexts(String language, HashMap<String, String> chosenLanguage,
			ArrayList<String> currentLanguages) {
		chosenLanguage.clear();
		currentLanguages.clear();
		BufferedReader reader = null;
		String line;
		String[] single_line;
		boolean level = false;
		try {

			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (level == true) {
					// stops the filling of chosenLanguage, because the current
					// line contains a new language
					if (line.contains("#")) {
						level = false;
					} else {
						// skip all lines without "="
						if (line.contains("=")) {
							single_line = line.split("=");
							try {
								chosenLanguage.put(single_line[0], single_line[1]);
							} catch (Exception e1) {
								chosenLanguage.put(single_line[0], "empty");
							}
						}
					}
				}
				// search the rigth line for the searched language
				if (line.contains(language)) {
					level = true;
				}
				// add all available languages to an ArrayList
				if (line.contains("#")) {
					single_line = line.split("#");
					currentLanguages.add(single_line[1].trim());
				}
				// add Time for the close Timer of the application to delivered
				// ArrayList
				if (line.contains("close_Time")) {
					single_line = line.split("=");
					chosenLanguage.put(single_line[0], single_line[1]);
				}
			}
			reader.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}
