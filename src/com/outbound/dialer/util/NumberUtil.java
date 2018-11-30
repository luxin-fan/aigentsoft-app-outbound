package com.outbound.dialer.util;

import java.util.Random;


public class NumberUtil {

	public static Random random = new Random();
	
	public static int getRandomNum(int scope){
		return random.nextInt(scope);
	}
}
