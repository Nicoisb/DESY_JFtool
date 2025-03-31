package freischaltungstool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger {

	private static final String PATH = "Freischaltungstool-Config\\user.log";

	/**
	 * Append the given String to a log-file
	 * 
	 * @param empty
	 *            ArrayListe<ArrayList<String>>
	 * 
	 * @return boolean
	 * 
	 */
	public boolean logMe(String entry) {
		// Format
		// lt + " " + user + ":" + "\t" + entry + "\n"
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date date = new Date();
		dateFormat.format(date);
		FileWriter fw;
		try {
			fw = new FileWriter(PATH, true);
			fw.write(dateFormat.format(date) + " " + System.getProperty("user.name").toLowerCase() + ": " + "\t" + entry
					+ "\r\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean getStatus(ArrayList<String> status) {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(PATH));

			for (String tmp; (tmp = reader.readLine()) != null;)
				if (status.add(tmp) && status.size() > 5)
					status.remove(0);

			reader.close();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

}
