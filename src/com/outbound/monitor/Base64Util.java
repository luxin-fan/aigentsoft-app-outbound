package com.outbound.monitor;

import org.apache.log4j.chainsaw.Main;

import com.sun.jersey.core.util.Base64;

public class Base64Util {
	final static Base64 base64 = new Base64();
	
	public static void main(String[] args) {
		System.out.println(encodeString("root"));
		System.out.println(encodeString("opensips321"));
	}
	
	public static String encodeString(String data){
		return new String(base64.encode(data));
	}
}
