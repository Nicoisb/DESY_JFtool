package freischaltungstool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ReadIni {

	private static final String PATH = "XNIMGRP.CFG";

	/**
	 * fills the passed ArrayList with all available packages for the chosen
	 * group
	 * 
	 * @param packages
	 * @param group
	 *            (Object)
	 * @return
	 */
	public boolean getPackages(ArrayList<String> packages, Object group) {
		String ignoredGroup;
		String searchedGroup;
		if (group.toString().contains("IT")) {
			searchedGroup = "#Dies sind die durch IT verwalteten Freischaltungsgruppen";
			ignoredGroup = "#Dies sind die durch IPP verwalteten Freischaltungsgruppen";
		} else {
			ignoredGroup = "#Dies sind die durch IT verwalteten Freischaltungsgruppen";
			searchedGroup = "#Dies sind die durch IPP verwalteten Freischaltungsgruppen";
		}

		packages.clear();
		BufferedReader reader = null;
		// needed for another method which saves all available package names to
		// a text-file
		ArrayList<String> allPackages = new ArrayList<String>();
		String line = null;
		String[] singleElement;
		boolean level = false;
		boolean checkGroup = false;
		boolean skipHeader = false;

		try {
			reader = new BufferedReader(new FileReader(PATH));

			while ((line = reader.readLine()) != null) {
				if (!line.contains("#")) {
					// set boolean "level" to true if the reader reach the area
					// where all available packages are listed
					if (line.contains("[Available Machinegroups]")) {
						level = true;
					}
				}
				if (level == true) {
					// set boolean "checkgroup" to true if the reader reach the
					// searched group inside the area where all available
					// packages are listed
					if (line.contains(searchedGroup)) {
						checkGroup = true;
					}
					// set boolean "checkgroup" to false if the reader reach the
					// other(ignored) group inside the area where all available
					// packages are listed
					if (line.contains(ignoredGroup)) {
						checkGroup = false;
					}
					// skip the line with the title of the area where all
					// available packages are listed, because it contains a char
					// which I filter after this clause to end adding packages
					// to the delivered ArrayList when the end of the area where
					// all available packages are listed is reached.
					if (skipHeader == true) {
						if (checkGroup == true) {
							// skip comments
							if (!line.contains("#")) {
								if (line.contains("=")) {
									singleElement = line.split("=");
									// the called method prove if the package is
									// listed too after the area where all
									// available packages are listed
									if (proveExistens(singleElement[0]) == true) {
										packages.add(singleElement[0]);
									}
								}
								// the end of the list Available Machinegroups
								if (line.contains("[")) {
									level = false;
								}
							}
						}
						/////// Write all available packages to an
						/////// ArrayList/////////////////
						if (!line.contains("#")) {
							if (line.contains("=")) {
								singleElement = line.split("=");
								allPackages.add(singleElement[0]);
							}
							// the end of the list Available Machinegroups
							if (line.contains("[")) {
								level = false;
							}
						}
					}
					skipHeader = true;
				}

			}
			reader.close();
			searchAllPackages(allPackages);
			// Sort ArrayList
			java.util.List<String> sublist = packages.subList(0, packages.size());
			Collections.sort(sublist);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * fills the passed ArrayList with all approved packages for the chosen
	 * pcname
	 * 
	 * 
	 * @return boolean
	 * 
	 */
	public boolean getPackageForPC(ArrayList<String> packages, String pcname) {
		packages.clear();
		String currentPackage = "";
		BufferedReader reader = null;
		String line = null;
		String[] singleElement;

		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (!line.contains("#")) {
					if (line.contains("[")) {
						currentPackage = line.replace("]", "");
						currentPackage = currentPackage.replace("[", "");
					}
				}
				if (line.contains("=")) {
					singleElement = line.split("=");
					// prove if pcname in ini-file contains the delivered pcname
					// and if they have the same length. So you can avoid the
					// case that the String in the delivered pcname contains
					// only parts of the pcname in the ini-file and not the
					// complete name
					if (singleElement[0].toLowerCase().contains(pcname.toLowerCase())
							& singleElement[0].trim().length() == pcname.trim().length()) {
						if (!line.contains("#")) {
							packages.add(currentPackage);
						}
					}
				}
			}
			reader.close();
			// Sort ArrayList
			if (packages.size() != 0) {
				java.util.List<String> sublist = packages.subList(0, packages.size());
				Collections.sort(sublist);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * prove if the package on top of the ini-file is too on the bottom the
	 * ini-file
	 * 
	 * @param searchedPackage
	 * @return
	 */
	private boolean proveExistens(String searchedPackage) {
		String searchedString = "[" + searchedPackage + "]";
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (line.contains(searchedString)) {
					reader.close();
					return true;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;

	}

	/**
	 * add all packages which are at the bottom of the ini-file too an ArrayList
	 * and calls the method savePackagestoTxt
	 * 
	 * 
	 * @return boolean
	 * 
	 */
	private void searchAllPackages(ArrayList<String> packagesTop) {
		BufferedReader reader = null;
		ArrayList<String> packagesDown = new ArrayList<>();
		String line = null;
		String name = null;
		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (line.contains("[")) {
					if (!line.contains("[Available Machinegroups]")) {
						name = line.replace("]", "");
						name = name.replace("[", "");

						packagesDown.add(name);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		savePackagesToTxt(packagesTop, packagesDown);
	}

	/**
	 * save the names of the packages on top of the ini-file and of the bottom
	 * the ini-file to a textfile
	 * 
	 * 
	 * @return boolean
	 * 
	 */
	public boolean savePackagesToTxt(ArrayList<String> packagesTop, ArrayList<String> packagesDown) {
		try {
			FileWriter writer = new FileWriter("Freischaltungstool-Config\\packages.txt");
			String input = "";
			// writer.append("LOCATION,PACKAGENAME");
			writer.append("Pakete die oben aufgelistet sind");
			writer.append("\r\n");
			writer.append("\r\n");

			for (int i = 0; i < packagesTop.size(); i++) {
				input = input + packagesTop.get(i);

				writer.append(input);
				writer.append("\r\n");
				input = "";
			}

			writer.flush();

			writer.append("\r\n");
			writer.append("Pakete die unten aufgelistet sind");
			writer.append("\r\n");
			writer.append("\r\n");
			for (int i = 0; i < packagesDown.size(); i++) {
				input = input + packagesDown.get(i);

				writer.append(input);
				writer.append("\r\n");
				input = "";
			}
			writer.close();
			return true;
		} catch (

		IOException e)

		{
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * add a new entry to the ini-file
	 * 
	 * @param name
	 * @param group
	 * @param comment
	 * @param selectedItem
	 * @param pcname
	 * @return
	 */
	public boolean writeNewEntry(String name, String group, String comment, Object selectedItem, String pcname) {
		BufferedReader reader = null;
		String line = "";
		String input = "";
		// same format like the title of package areas in the ini-file
		String searchedPackage = "[" + selectedItem + "]";
		boolean checkPackage = false;
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		comment = " " + comment;
		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (checkPackage == true) {
					// check if a new package is reached
					if (!line.contains("[")) {
						// skip comments
						if (!line.contains("#")) {
							if (!line.contains("=")) {
								input = input + pcname + "=" + name + " " + group.toUpperCase() + " "
										+ dateFormat.format(date) + " " + getUsername() + comment
										+ " |Bearbeitet(eingetragen) durch GUI: " + getUsername() + " "
										+ dateFormat.format(date) + "\r\n";
								checkPackage = false;
							}
						}

					}
				}
				if (line.contains(searchedPackage)) {
					checkPackage = true;
				}
				input = input + line + "\r\n";
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter writer = new FileWriter(PATH);
			writer.write(input);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public String getUsername() {
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new FileReader("XNIMGRP.CFG.lck"));
			while ((line = reader.readLine()) != null) {
				return line.trim();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * delete the entry for the delivered pcname in the delivered package
	 * 
	 * @param pcname
	 * @param delPackage
	 * @return
	 */
	public boolean deleteEntry(String pcname, String delPackage) {
		BufferedReader reader = null;
		String line = "";
		String input = "";
		String[] singleElement;
		boolean checkPackage = false;

		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (checkPackage == true) {
					if (line.contains("=")) {
						singleElement = line.split("=");

						if (!line.contains(pcname) || line.contains("#")) {
							input = input + line + "\r\n";
						} else {
							if (singleElement[0].trim().length() == pcname.trim().length()) {
								checkPackage = false;
							} else
								input = input + line + "\r\n";
						}
					} else
						input = input + line + "\r\n";
				} else {
					input = input + line + "\r\n";
				}
				if (line.contains(delPackage)) {
					checkPackage = true;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter writer = new FileWriter(PATH);
			writer.write(input);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * comment the old entry out and set under this entry the new entry for the
	 * other delivered pcname
	 * 
	 * @param name
	 * @param group
	 * @param comment
	 * @param newpcname
	 * @param oldpcname
	 * @param selectedPackage
	 * @return
	 */
	public boolean transferEntry(String name, String group, String comment, String newpcname, String oldpcname,
			String selectedPackage) {
		BufferedReader reader = null;
		String line = "";
		String input = "";
		String oldLine = "";
		boolean checkPackage = false;
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		comment = " " + comment;

		try {
			reader = new BufferedReader(new FileReader(PATH));
			while ((line = reader.readLine()) != null) {
				if (checkPackage == true) {
					if (line.contains(oldpcname) && !line.contains("#")) {
						oldLine = "#" + line + "\r\n";
						input = input + oldLine + newpcname + "=" + name + " " + group.toUpperCase() + " "
								+ dateFormat.format(date) + " " + getUsername() + comment
								+ " |Bearbeitet(übertragen) durch GUI: " + getUsername() + " " + dateFormat.format(date)
								+ "\r\n";
						checkPackage = false;
					} else {
						input = input + line + "\r\n";
					}

				} else {
					input = input + line + "\r\n";
				}
				if (line.contains(selectedPackage)) {
					checkPackage = true;
				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter writer = new FileWriter(PATH);
			writer.write(input);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
