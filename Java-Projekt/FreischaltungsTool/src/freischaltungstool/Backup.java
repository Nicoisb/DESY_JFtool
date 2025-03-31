package freischaltungstool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Backup {

	/**
	 * create a folder "Backup" if it's doesn't exist and create a copy of the
	 * current ini-file in the "Backup" folder with name of application-user and
	 * the current time "dd.MM.yyyy_HH_mm"
	 * 
	 * @param empty
	 *            ArrayListe<ArrayList<String>>
	 * 
	 * @return boolean
	 * 
	 */
	public boolean createBackup() {
		File dir = new File("Backup");
		dir.mkdir();
		String filename;

		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy_HH_mm");
		// get current date time with Date()
		Date date = new Date();

		filename = "XNIMGRP_" + System.getProperty("user.name").toLowerCase() + "_" + dateFormat.format(date) + ".CFG";

		Path source = Paths.get("XNIMGRP.CFG");
		Path target = Paths.get("Backup\\" + filename);
		try {
			Files.copy(source, target);
		} catch (IOException e1) {
			return false;
		}
		return true;
	}
}
