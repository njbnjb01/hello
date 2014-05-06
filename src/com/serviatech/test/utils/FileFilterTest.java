package com.serviatech.test.utils;

import java.io.File;
import java.io.FileFilter;

public class FileFilterTest implements FileFilter {

	/**
	 * @param args
	 */
	String condition1 = "";
	String condition2 = "";

	public FileFilterTest(String condition1,String condition2) {
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	
	
	@Override
	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		String filename = pathname.getName();
		if (filename.lastIndexOf(condition1) != -1 || filename.lastIndexOf(condition1.toUpperCase()) != -1 || filename.lastIndexOf(condition2) != -1 || filename.lastIndexOf(condition2.toUpperCase()) != -1) {
			return true;
		} else
			return false;
	}

}
