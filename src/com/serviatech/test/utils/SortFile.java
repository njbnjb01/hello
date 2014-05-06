package com.serviatech.test.utils;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * 文件排序
 * 
 * @author grid
 * 
 */
public class SortFile {

	/**
	 * 根据文件名称排序
	 * 
	 */
	public void sortedByName(String[] names) {
		Comparator<String> comparator = new FileNameComparator();
		Arrays.sort(names, comparator);
	}

}
