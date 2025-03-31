package freischaltungstool;

import java.io.File;

public class AMSLock {
	private File lockFile = new File("XNIMGRP.CFG.swp");

	public boolean checkaccess() {
		if (lockFile.exists() == true) {
			return true;
		} else {
			return false;
		}
	}

}
