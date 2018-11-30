package com.outbound.conf;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InitConfig {
	
	private static Logger logger = Logger.getLogger(InitConfig.class.getName());
	
	public static String PORT;
	
	public static String BIND_IP;
	
	public static String DB_URL;
	
	public static String DB_PWD;
	
	public static String OUTBOUND_URL;
	
	public static String PENNY_URL;
	
	public static String INSTANCE_ID;
	
	public static boolean init() throws ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element theContext = null, root = null;
		
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = factory.newDocumentBuilder();
	
		String sp = System.getProperty("file.separator");
		String filePath = "conf" + sp + "conf.xml";
		Document xmldoc = null;
		File file = null;
		try{
			file = new File(filePath);
			xmldoc = db.parse(file);
		}catch (Exception e) {
			logger.error(" Server Default Config File  Not Found !");
			return false;
		}
		
		root = xmldoc.getDocumentElement();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='port']",root);
		if(theContext != null)
			PORT = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='bind_ip']",root);
		if(theContext != null)
			BIND_IP = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='db_url']",root);
		if(theContext != null)
			DB_URL = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='db_pwd']",root);
		if(theContext != null)
			DB_PWD = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='outbound_url']",root);
		if(theContext != null)
			OUTBOUND_URL = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='penny_url']",root);
		if(theContext != null)
			PENNY_URL = theContext.getTextContent().trim();
		
		theContext = (Element) selectSingleNode(
				"/config/variable[@name='instance_id']",root);
		if(theContext != null)
			INSTANCE_ID = theContext.getTextContent().trim();

		logger.info("Config File Init Complete !");
		return true;
	}
	
	public static Node selectSingleNode(String express, Object source) {
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}
}
