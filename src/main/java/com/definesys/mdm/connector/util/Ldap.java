package com.definesys.mdm.connector.util;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * 
 * @author liw109370
 *
 */

@Slf4j
@PropertySource("classpath:application.yml")
@Service
public class Ldap {

	private static String LDAP_URL;

	@Value(value = "${ad.newBaseUri}")
	public void setLdapUrl(String ldapUrl) {
		Ldap.LDAP_URL = ldapUrl;
	}

	/**
	 * 域认证
	 * @param loginName
	 * @param password
	 * @return
	 */
	public static boolean  domainAuthenticate(String loginName,String password){
		log.info("loginName&password========>"+loginName+"&"+password);
		boolean authFlag = false;
		DirContext dc = null;
	    Hashtable<String,String> env = new Hashtable<String,String>();
	    if(!loginName.endsWith("@hanslaser.com")){
	    	loginName += "@hanslaser.com";
		}
	    env.put("java.naming.factory.initial", 
	      "com.sun.jndi.ldap.LdapCtxFactory");
		log.info("LDAP_URL========>"+LDAP_URL);
	    env.put("java.naming.provider.url", LDAP_URL);
	    env.put("java.naming.security.authentication", "simple");
	    env.put("java.naming.security.principal", loginName);
	    env.put("java.naming.security.credentials", password);
	    try {
	    	dc = new InitialDirContext(env);
	      	authFlag = true;
	      	log.info("loginName="+loginName+",password="+password+",认证成功");
	    } catch (AuthenticationException e) {
	    	log.info("loginName="+loginName+",password="+password+",认证失败:"+e.getMessage());
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	log.info("loginName="+loginName+",password="+password+",认证异常:"+e.getMessage());
	    }finally{
	    	if(dc!=null){
	    		try {
					dc.close();
				} catch (NamingException e) {
					log.info("loginName="+loginName+",password="+password+",关闭dc异常:"+e.getMessage());
				}
	    	}
	    }
	    return authFlag;
	}



}
