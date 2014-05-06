package com.serviatech.test.utils;


import java.io.File;

public class SortFileTest {
	public static String path = "C:/Program Files/Tencent/QQ/Users/283536963/FileRecv/images";
	public static void main(String[] args) {

		File file = new File(path);
		File[] files = file.listFiles(new FileFilterTest(".png",".gif"));
		String[] array = new String[files.length];
		for (int j = 0; j < files.length; j++) {
			array[j] = files[j].getAbsolutePath();
			System.out.println("before sort");
			System.out.println(files[j].getAbsolutePath());
		}
		long t1 = System.nanoTime();
		SortFile sortFile = new SortFile();
		long t2 = System.nanoTime();
		System.out.println(t2 - t1);

		sortFile.sortedByName(array);

		for (String mfile : array) {
			System.out.println("after sort");
			System.out.println(mfile);
		}
	}
}
