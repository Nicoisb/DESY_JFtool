package freischaltungstool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NSLookup {

	public boolean prove(String pcname) {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { "nslookup", pcname });
			BufferedReader bi = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = "";
			line = bi.readLine();
			while (line != null) {
				// System.out.println(line);
				if (line.indexOf("Non-existent") != -1) {
					bi.close();
					p.destroy();
					return false;
				}
				line = bi.readLine();
			}
			bi.close();
			p.destroy();
			return true;

		} catch (IOException e) {
			return false;
		}

	}

}