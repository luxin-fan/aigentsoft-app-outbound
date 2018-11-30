package com.outbound.object.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class GsonFactory {
	
	

	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
	    
    private static volatile Gson gson = GSON_BUILDER.create();
    
    private static volatile Gson gsonBuilder = GSON_BUILDER.serializeNulls().create();
    
    
    /**
     * 获取Gson实例.
     * 
     * @return Gson实例
     */
    public static Gson getGson() {
        return gson;
    }
    
    /**
     * 获取Gson实例.
     * 
     * @return Gson实例
     */
    public static Gson getBuildGson() {
        return gsonBuilder;
    }
	
}
