package com.outbound.dialer.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.outbound.common.Util;


/**
 * ftp服务器信息 
 * @author duanlsh
 *
 */
public class FTPOperatUtil {

	private FTPClient ftp = null;


	/**
	 * 连接FTP服务器
	 * 
	 * @param server
	 * @param uname
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean connectFTPServer(String server, int port, String uname,
			String password) throws Exception{

			ftp = new FTPClient();
			ftp.setDefaultTimeout(15000);
			ftp.connect(server, port);
			// 文件类型,默认是ASCII
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 设置被动模式
			ftp.enterLocalPassiveMode();

			ftp.setControlEncoding("utf-8");
			boolean result = ftp.login(uname, password);

			// 响应信息
			int replyCode = ftp.getReplyCode();
			if ((!FTPReply.isPositiveCompletion(replyCode))) {
				// 关闭Ftp连接
				closeFTPClient();
				// 释放空间
				ftp = null;
				throw new IllegalArgumentException("登录FTP服务器失败,请检查![Server:" + server + "、"
						+ "User:" + uname + "、" + "Password:***");
			} 
			return result;
	}


	/**
	 * 上传文件到ftp 不支持中文名字
	 * @param localFile
	 * @param newName
	 * @throws Exception
	 */
	public void uploadFile(String localFile,String localFileName, String newName) throws Exception {
		InputStream input = null;
		try {
			File file = null;
			if (checkFileExist(localFile, localFileName, false)) {
				file = new File(localFile);
			}
			input = new FileInputStream(file);
			boolean result = ftp.storeFile(newName, input);
			if (!result) {
				throw new Exception("文件上传失败!");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}


	/**
	 * 从FTP指定的路径下载文件
	 * 
	 * @param remotePath
	 * @param localPath
	 */
	public void downFile(String remotePath, String localPath, String localFileName)
			throws Exception {

		OutputStream output = null;
		try {
			File file = null;
			if (checkFileExist(localPath, localFileName, true)) {
				file = new File(localPath + File.separator + localFileName);
			}
			output = new FileOutputStream(file);
			boolean result = ftp.retrieveFile(remotePath, output);
			if (!result) {
				throw new Exception("文件下载失败!");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (output != null) {
				output.close();
			}
		}

	}
	
	

	/**
	 * 从FTP指定的路径下载文件
	 * 
	 * @param remoteFilePath
	 * @return
	 * @throws Exception
	 */
	public InputStream downFile(String remoteFilePath) throws Exception {
		ftp.enterLocalPassiveMode(); 
		ftp.setFileType(FTP.BINARY_FILE_TYPE); 
	    FTPFile[] files = ftp.listFiles(remoteFilePath);
	    if (files.length != 1)
	    	throw new FileNotFoundException("ftp: " + remoteFilePath);
	  
		return ftp.retrieveFileStream(remoteFilePath);
	}
	
	
	/**
	 * 从ftp指定路径下载文件
	 * @param remoteFilePath
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public BufferedReader downFileBufferedReader(String remoteFilePath) throws UnsupportedEncodingException, Exception{
		
		return new BufferedReader(new InputStreamReader(this.downFile(remoteFilePath), "UTF-8"));
	}


	/**
	 * 检查文件是否存在
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private boolean checkFileExist(String filePath, String fileName, boolean isCreateFile) throws Exception {
		boolean flag = false;
		File file = new File(filePath + File.separator + fileName);
		if (!file.exists()) {
			if (isCreateFile) {
				createFile(filePath, fileName);
				return !flag;
			}
			return flag;
		}
		return !flag;
	}
	
	
	/**
	 * 创建文件信息
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	private void createFile(String filePath, String fileName) throws IOException{
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = filePath + File.separator + fileName;
		file = new File(path);
		if (!file.exists())
			file.createNewFile();
	}

	
	
	/**
	 * 删除远程文件
	 * @param remoteFilePath
	 * @return
	 * @throws IOException
	 */
	public boolean deleteRemoteFile(String remoteFilePath) throws IOException{
//		ftp.enterLocalPassiveMode(); 
//		ftp.setFileType(FTP.BINARY_FILE_TYPE); 
		boolean status = false;
//	    FTPFile[] files = ftp.listFiles(remoteFilePath); 
//	    if (files.length >= 1){ 
	    	status = ftp.deleteFile(remoteFilePath); 
//	    } 
	    return status; 
	}
	
	
	/**
	 * 重命名远程文件
	 * @param name
	 * @param remote
	 * @return
	 * @throws IOException
	 */
	public boolean rename(String name,String remote) throws IOException { 
		ftp.enterLocalPassiveMode(); 
		ftp.setFileType(FTP.BINARY_FILE_TYPE); 
	    boolean result = false; 
	    FTPFile[] files = ftp.listFiles(remote); 
	    if (files.length >= 1)
	    	result = ftp.rename(remote, name); 
	    return result; 
	} 

	
	/**
	 * 关闭FTP连接
	 * 
	 * @param ftp
	 * @throws Exception
	 */
	private void closeFTPClient(FTPClient ftp){
		try {
			if (ftp != null){
				if (ftp.isConnected())
					ftp.disconnect();
			}
		} catch (Exception e) {
			Util.error(FTPOperatUtil.class, "Exception  function:closeFTPClient",e);
		}
	}

	/**
	 * 关闭FTP连接
	 * 
	 * @throws Exception
	 */
	public void closeFTPClient() {

		this.closeFTPClient(this.ftp);

	}



	/**
	 * 主方法(测试)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			FTPUtil fu = new FTPUtil("172.16.20.107", 21, "duanlsh", "duanlsh");
//			fu.uploadFile("C:\\Users\\duanlsh\\Desktop\\规范.txt", "规范.txt"); 
//			fu.downFile("git.pptx", "E:\\localftp\\test\\", "git.pptx");
//			System.out.println(fu.deleteRemoteFile("aa.txt"));
//			fu.closeFTPClient();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("异常信息：" + e.getMessage());

		}
	}
	
}

