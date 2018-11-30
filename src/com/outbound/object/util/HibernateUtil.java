package com.outbound.object.util;

import org.hibernate.HibernateException;  
import org.hibernate.Session;  
import org.hibernate.SessionFactory;  
import org.hibernate.cfg.Configuration;  
  
public class HibernateUtil {  
    private static final SessionFactory sessionFactory;  
    private static final ThreadLocal m_session = new ThreadLocal();  
      
    static {  
        try{  
            sessionFactory = new Configuration().configure().buildSessionFactory();  
        }catch(HibernateException ex){  
            throw new RuntimeException("创建SessionFactory失败: " + ex.getMessage(), ex);  
        }  
    }  
      
    public static Session currentSession() throws HibernateException {  
        Session s = (Session) m_session.get();  
        if (s == null) {  
            s = getSessionfactory().openSession();  
            m_session.set(s);  
        }  
        return s;  
    }  
      
    public static void closeSession() throws HibernateException {  
        Session s = (Session) m_session.get();  
        m_session.set(null);  
        if (s != null)  
            s.close();  
    }

	public static SessionFactory getSessionfactory() {
		return sessionFactory;
	}  
}  