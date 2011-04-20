package univ_fcomte.synchronisation;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

public class Synchronisation {

	private static final int HTTP_STATUS_OK = 200;
	private static String sUserAgent = null;
	private String serveur;
	private Context context;
	
	public Synchronisation(Context context, String serveur) {
		this.serveur = serveur;
		this.context = context;
		prepareUserAgent();
	}
	
	public static class ApiException extends Exception {
		private static final long serialVersionUID = 1L;
		public ApiException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
		public ApiException(String detailMessage) {
			super(detailMessage);
		}
	}

	public static class ParseException extends Exception {
		private static final long serialVersionUID = 1L;
		public ParseException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
	}
	
	public void prepareUserAgent() {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			sUserAgent = String.format("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"/*context.getString(R.string.template_user_agent)*/, info.packageName, info.versionName);
		} catch (NameNotFoundException e) {
			Log.e("Synchronisation", "Couldn't find package information in PackageManager", e);
		}
	}
	
	//=======================================================
	// Recupère une page Web
	//=======================================================
	public synchronized String GetHTML(List <NameValuePair> nvps) throws ApiException {
		
		if (sUserAgent == null)
			throw new ApiException("User-Agent string must be prepared");
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpHost proxy = new HttpHost(PreferenceManager.getDefaultSharedPreferences(context).getString("adresse_proxy", ""), PreferenceManager.getDefaultSharedPreferences(context).getInt("port", 0000));
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("utilise_proxy", false))
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		try {
			HttpResponse res;
			URI uri = new URI(serveur);
			
			if (nvps!=null){
	    		HttpPost methodpost = new HttpPost(uri);
	    		methodpost.addHeader("pragma","no-cache");
	    		//methodpost.setHeader("User-Agent", sUserAgent);
	    		methodpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    		
	    		//timeout
	    		HttpConnectionParams.setConnectionTimeout(methodpost.getParams(), 5000);
	    		HttpConnectionParams.setSoTimeout(methodpost.getParams(), 20000);
	    		
	    		res = httpClient.execute(methodpost);
		    } else {
	    		HttpGet methodget = new HttpGet(uri);
		    	methodget.addHeader("pragma","no-cache");
		    	//methodget.setHeader("User-Agent", sUserAgent);
	    		
		    	//timeout
	    		HttpConnectionParams.setConnectionTimeout(methodget.getParams(), 5000);
	    		HttpConnectionParams.setSoTimeout(methodget.getParams(), 20000);
		    	
	    		res = httpClient.execute(methodget);
		    }
			
			StatusLine status = res.getStatusLine();
			if (status.getStatusCode() != HTTP_STATUS_OK)
				throw new ApiException("Invalid response from server: " + status.toString());
			
	    	InputStream data = res.getEntity().getContent();
	    	
	    	return stream2String(data);
	    		    	        	
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	//=======================================================
	// GenerateString 
	//=======================================================
	static public String stream2String(InputStream stream) {
		
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		try { 
			String cur;   
			while ((cur = buffer.readLine()) != null) {   
				sb.append(cur).append("\n");  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}
	
	public String md5(String s) {
		
		try {
			
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "";
		
	}
	
}
