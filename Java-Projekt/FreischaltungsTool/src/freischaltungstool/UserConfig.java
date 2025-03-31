package freischaltungstool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserConfig {

	private final static String PATH = "Freischaltungstool-Config\\userconfig.txt";

	// create the config file
	public UserConfig() {
		FileWriter fw;
		try {
			fw = new FileWriter(PATH, true);
			fw.write("");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * returns the config of the user if he has an entry in the config-file. If
	 * there is no entry it returns a default config
	 * 
	 * @return
	 */
	public String[] getUserConfig() {

		BufferedReader reader = null;
		String line;
		String[] user;
		String[] config = { "IT", "Deutsch(german)" };

		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (line.contains(System.getProperty("user.name").toLowerCase())) {
					user = line.split("=");
					config = user[1].split(",");
				}
			}
			reader.close();
			return config;
		} catch (Exception e) {
		}
		return config;
	}

	/**
	 * 
	 * overwrite the config of the user or write a new entry for the user if
	 * he/she has no entry
	 * 
	 * @param group
	 * @param language
	 * @return
	 */
	public boolean setUserConfig(Object group, String language) {
		BufferedReader reader = null;
		String line;
		boolean exist = false;
		try {
			reader = new BufferedReader(new FileReader(PATH));
			// prove if the user has already an entry in the config-file
			while ((line = reader.readLine()) != null) {
				if (line.contains(System.getProperty("user.name").toLowerCase())) {
					exist = true;
				}
			}
			reader.close();
		} catch (Exception e) {
			return false;
		}
		// add new entry to txt if the user has no config
		if (exist == false) {
			FileWriter fw;
			try {
				fw = new FileWriter(PATH, true);
				fw.write(System.getProperty("user.name").toLowerCase() + "=" + group + "," + language + "\r\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		// overwrite old config entry of the user
		else {
			String line2 = "";
			String input = "";

			try {
				reader = new BufferedReader(new FileReader(PATH));
				while ((line2 = reader.readLine()) != null) {
					if (line2.contains(System.getProperty("user.name").toLowerCase()))
						input = input + System.getProperty("user.name").toLowerCase() + "=" + group + "," + language
								+ "\r\n";
					else
						input = input + line2;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			FileWriter fw;
			try {
				fw = new FileWriter(PATH);
				fw.write(input);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
