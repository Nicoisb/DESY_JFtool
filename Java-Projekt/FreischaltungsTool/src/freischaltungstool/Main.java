package freischaltungstool;

import java.io.File;

public class Main {

	public static void main(String[] args) {

		GUI window = new GUI();
		Backup backup = new Backup();
		backup.createBackup();
		File dir = new File("Freischaltungstool-Config");
		dir.mkdir();
		UserConfig userconfig = new UserConfig();

		// // String to be scanned to find the pattern.
		// String line = "This order was placed for QT3000! OK?";
		// String pattern = "^[^#=\\]\\[]*$";
		//
		// // Create a Pattern object
		// Pattern r = Pattern.compile(pattern);
		//
		// // Now create matcher object.
		// Matcher m = r.matcher(line);
		// if (m.find()) {
		// System.out.println("Alles gut");
		// } else {
		// System.out.println("Nicht erlaubt");
		// }

	}

}
