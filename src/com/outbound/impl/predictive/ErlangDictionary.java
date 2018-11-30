package com.outbound.impl.predictive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ErlangDictionary implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(ErlangDictionary.class);

	private HashMap<Integer, HashMap<Float, Float>> dictionary = new HashMap<Integer, HashMap<Float, Float>>();
	private static ErlangDictionary instance = new ErlangDictionary();

	public static ErlangDictionary getInstance() {
		return instance;
	}

	private ErlangDictionary() {
	}
	
	public float calculateErlang(int ready, float abadon_rate){
		HashMap<Float, Float> erlangMap = dictionary.get(ready);
		if(erlangMap == null){
			return 0.0f;
		}
		if(abadon_rate <= 0.0001){
			return erlangMap.get(0.01f);
		}else if(abadon_rate > 0.0001 && abadon_rate <= 0.0005){
			return erlangMap.get(0.05f);
		}else if(abadon_rate > 0.0005 && abadon_rate <= 0.0010){
			return erlangMap.get(0.10f);
		}else if(abadon_rate > 0.0010 && abadon_rate <= 0.0020){
			return erlangMap.get(0.20f);
		}else if(abadon_rate > 0.0020 && abadon_rate <= 0.0050){
			return erlangMap.get(0.50f);
		}else if(abadon_rate > 0.0050 && abadon_rate <= 0.01){
			return erlangMap.get(1.00f);
		}else if(abadon_rate > 0.01 && abadon_rate <= 0.015){
			return erlangMap.get(1.50f);
		}else if(abadon_rate > 0.015 && abadon_rate <= 0.02){
			return erlangMap.get(2.00f);
		}else if(abadon_rate > 0.02 && abadon_rate <= 0.025){
			return erlangMap.get(2.50f);
		}else if(abadon_rate > 0.025 && abadon_rate <= 0.03){
			return erlangMap.get(3.00f);
		}else if(abadon_rate > 0.03 && abadon_rate <= 0.04){
			return erlangMap.get(4.00f);
		}else if(abadon_rate > 0.04 && abadon_rate <= 0.051f ){
			return erlangMap.get(5.00f);
		}
		
		return 0.0f;
	}

	public void reload() {

		Erlang[] cs = Erlang.values();
		for (Erlang c : cs) {

			InputStream is = getClass().getClassLoader().getResourceAsStream(
					c.getConfigFileName());
			if (is == null) {
				logger.error("Reloading canceled. {} not exists. ",
						c.getConfigFileName());
				return;
			}
			try {
				load(is, getDictionary());
			} catch (NumberFormatException e) {
				logger.error("Reloading canceled. " + e.getMessage(), e);
				return;
			} catch (IOException e) {
				logger.error("Reloading canceled. " + e.getMessage(), e);
				return;
			}
		}
	}

	static void load(InputStream is,
			HashMap<Integer, HashMap<Float, Float>> dictionary)
			throws NumberFormatException, IOException {
		InputStreamReader fr = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue;
			}
			String[] splits = line.split(",");
			if (splits.length < 12) {
				continue;
			}
			String h = splits[0];
			if (h.length() == 0) {
				continue;
			}

			int agentNum = Integer.valueOf(splits[0]);
			HashMap<Float, Float> values = dictionary.get(agentNum);
			if (values == null) {
				values = new HashMap<Float, Float>();
				values.put(0.01f, Float.valueOf(splits[1]));
				values.put(0.05f, Float.valueOf(splits[2]));
				values.put(0.10f, Float.valueOf(splits[3]));
				values.put(0.20f, Float.valueOf(splits[4]));
				values.put(0.50f, Float.valueOf(splits[5]));
				values.put(1.00f, Float.valueOf(splits[6]));
				values.put(1.50f, Float.valueOf(splits[7]));
				values.put(2.00f, Float.valueOf(splits[8]));
				values.put(2.50f, Float.valueOf(splits[9]));
				values.put(3.00f, Float.valueOf(splits[10]));
				values.put(4.00f, Float.valueOf(splits[11]));
				values.put(5.00f, Float.valueOf(splits[12]));
			}
			dictionary.put(agentNum, values);
		}
		System.out.println("dictionary size " + dictionary.size());
		br.close();
	}

	@Override
	public void run() {
		try {
			reload();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

	}

	public HashMap<Integer, HashMap<Float, Float>> getDictionary() {
		return dictionary;
	}

	public void setDictionary(HashMap<Integer, HashMap<Float, Float>> dictionary) {
		this.dictionary = dictionary;
	}

	public static void main(String... args) throws IOException {
		ErlangDictionary.getInstance().reload();
		System.out.println(ErlangDictionary.getInstance().getDictionary()
				.size());
	}

}
