package com.serviatech.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.content.Context;

public class AppUtils {
	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static void copyFile(String oldPath, String newPath) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (!oldfile.exists()) { //文件不存在时 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   System.out.println(bytesum); 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   } 

   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
	public static void copyFolder(String oldPath, String newPath) throws Exception {

			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			if (file != null && file.length>0) {
				for (int i = 0; i < file.length; i++) {
					if (oldPath.endsWith(File.separator)) {
						temp = new File(oldPath + file[i]);
					} else {
						temp = new File(oldPath + File.separator + file[i]);
					}
					
					if (temp.isFile()) {
						FileInputStream input = new FileInputStream(temp);
						FileOutputStream output = new FileOutputStream(newPath
								+ "/" + (temp.getName()).toString());
						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					if (temp.isDirectory()) {// 如果是子文件夹
						copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
					}
				}
			}
	}
	// 复制文件    
		public static void copyFiles(File sourceFile,File targetFile) throws IOException{  
		        // 新建文件输入流并对它进行缓冲    
		        FileInputStream input = new FileInputStream(sourceFile);  
		        BufferedInputStream inBuff=new BufferedInputStream(input);  
		  
		        // 新建文件输出流并对它进行缓冲    
		        if (!targetFile.getParentFile().exists()) {
		        	targetFile.getParentFile().mkdirs();
				}
		        FileOutputStream output = new FileOutputStream(targetFile);  
		        BufferedOutputStream outBuff=new BufferedOutputStream(output);  
		          
		        // 缓冲数组    
		        byte[] b = new byte[1024 * 5];  
		        int len;  
		        while ((len =inBuff.read(b)) != -1) {  
		            outBuff.write(b, 0, len);  
		        }  
		        // 刷新此缓冲的输出流    
		        outBuff.flush();  
		          
		        //关闭流    
		        inBuff.close();  
		        outBuff.close();  
		        output.close();  
		        input.close();  
		    }
		// 复制文件夹    
	    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {  
	        // 新建目标目录    
	        (new File(targetDir)).mkdirs();  
	        // 获取源文件夹当前下的文件或目录    
	        File[] file = (new File(sourceDir)).listFiles();  
	        for (int i = 0; i < file.length; i++) {  
	            if (file[i].isFile()) {  
	                // 源文件    
	                File sourceFile=file[i];  
	                // 目标文件    
	               File targetFile=new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());  
	                copyFiles(sourceFile,targetFile);  
	            }  
	            if (file[i].isDirectory()) {  
	                // 准备复制的源文件夹    
	                String dir1=sourceDir + "/" + file[i].getName();  
	                // 准备复制的目标文件夹    
	                String dir2=targetDir + "/"+ file[i].getName();  
	                copyDirectiory(dir1, dir2);  
	            }  
	        }  
	    }
	/**
	 * 得到屏保页面滚动文字
	 * @param path
	 */
	public static String getText(String path) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		File file = new File(path);
		if (!file.exists()) {
			return "";
		}
		BufferedReader br;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GB2312");
			br = new BufferedReader(read);
			String line = "";
			while((line = br.readLine())!=null){
				sb.append(line);
			}
			br.close();
			return sb.toString();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
