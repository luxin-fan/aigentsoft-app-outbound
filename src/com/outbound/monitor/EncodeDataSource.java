package com.outbound.monitor;
import com.sun.jersey.core.util.Base64;

import org.apache.commons.dbcp.*;

public class EncodeDataSource extends BasicDataSource{
	
	public EncodeDataSource(){
		super();
	}

	@Override
	public void setPassword(String password){
		try {
			System.out.println(password);
			this.password =new String(Base64.decode(password));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void setUsername(String userName){
		try {
			System.out.println(userName);
			this.username =new String(Base64.decode(userName));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

}
