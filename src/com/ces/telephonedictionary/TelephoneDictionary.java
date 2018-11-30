package com.ces.telephonedictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 *
 */
public class TelephoneDictionary implements Runnable{
	
	private static final Logger logger = Logger
			.getLogger(TelephoneDictionary.class);
	private volatile HashMap<String, String> dictionary = new HashMap<String, String>();
	private static TelephoneDictionary instance = new TelephoneDictionary();
	private boolean inited = false;
	public static TelephoneDictionary getInstance(){
		return instance;
	}
	private TelephoneDictionary(){
		
	}
	public void init(){
		if(inited == false){
			synchronized (TelephoneDictionary.class) {
				if(inited == false){
					run();
					Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 1, 1, TimeUnit.HOURS);
					inited = true;
				}
			}
		}
	}
	public void reload(){
		HashMap<String, String> dictionary = new HashMap<String, String>();
		Carrier[] cs = Carrier.values();
		for (Carrier c : cs) {
			
			InputStream is = getClass().getClassLoader().getResourceAsStream(c.getConfigFileName());

			if(is == null){
				logger.error("Reloading canceled. "+ c.getConfigFileName() +" not exists. ");
				return;
			}
			try {
				load(is, dictionary, c);
			} catch (NumberFormatException e) {
				logger.error("Reloading canceled. " + e.getMessage(), e);
				return;
			} catch (IOException e) {
				logger.error("Reloading canceled. " + e.getMessage(), e);
				return;
			}
		}
		this.dictionary = dictionary;
	}
	
	public String getArea(String phone){
		if(phone==null || phone.length() <= 7){
			return null;
		}
		String prefix = phone.substring(0,7);
		String area = dictionary.get(prefix);
		if(area == null){
			if(phone.length() >= 8){
				prefix = phone.substring(0,8);
				area = dictionary.get(prefix);
			}
		}
	//	System.out.println(dictionary.size());
		return area;
	}
	
	static void load(InputStream is,HashMap<String, String> dictionary, Carrier carrier ) throws NumberFormatException, IOException{
		InputStreamReader fr = new InputStreamReader(is,"utf-8");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine()) != null){
			if(line.trim().isEmpty()){
				continue;
			}
			String [] splits = line.split(",");
			if(splits.length < 2 ){
				continue;
			}
			String h = ptrimStr(splits[0]);
			String city = ptrimStr(splits[1]);
			dictionary.put(h.trim(), city.trim());
		}
		br.close();
		logger.info("##@## mobile region size [" + dictionary.size() +"]");
	}

	static String ptrimStr(String str){
		if(str != null && str.startsWith("\"")){
			str = str.substring(1,str.length());
			if(str.endsWith("\"")){
				str = str.substring(0,str.length()-1);
			}
		}
		return str;
	}
	
	@Override
	public void run() {
		try{
			reload();
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
		}
		
	}

	public static void main(String ... args) throws IOException {
		
		TelephoneDictionary dict= new TelephoneDictionary();
		dict.reload();
		System.out.println(dict.getArea("17321061617"));

	}
}
