package univ_fcomte.synchronisation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Synchronisation {

	private static final int HTTP_STATUS_OK = 200;

	private static String sUserAgent = null;
	
	public Synchronisation(Context context) {
		prepareUserAgent(context);
	}
	
	public static class ApiException extends Exception {
		public ApiException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
		public ApiException(String detailMessage) {
			super(detailMessage);
		}
	}

	public static class ParseException extends Exception {
		public ParseException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
	}
	
	public static void prepareUserAgent(Context context) {
		try {
			// Read package name and version number from manifest
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
	public synchronized String GetHTML(String url, List <NameValuePair> nvps) throws ApiException {
		
		if (sUserAgent == null)
			throw new ApiException("User-Agent string must be prepared");
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse res;
			URI uri = new URI(url);
			
			if (nvps!=null){
	    		HttpPost methodpost = new HttpPost(uri);
	    		methodpost.addHeader("pragma","no-cache");
	    		//methodpost.setHeader("User-Agent", sUserAgent);
	    		methodpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    		res = httpClient.execute(methodpost);
		    } else {
	    		HttpGet methodget = new HttpGet(uri);
		    	methodget.addHeader("pragma","no-cache");
		    	//methodget.setHeader("User-Agent", sUserAgent);
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
	
	public boolean envoyerJson(String url, JSONObject json) {
		
		boolean reussi = false;
		
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
		HttpResponse response;
		try{
			HttpPost post = new HttpPost(url);
			//StringEntity se = new StringEntity("JSON: " + json.toString());
			//se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);  
	        nameValuePairs.add(new BasicNameValuePair("json", "qfqv"));  
	        
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			
			//post.setEntity(se);
			
			response = client.execute(post);
			/*Checking response */
			if(response!=null){
				if (response.getStatusLine().getStatusCode() != HTTP_STATUS_OK)
					reussi = true;
				InputStream in = response.getEntity().getContent(); //Get the data in the entity
				//Log.i("Reponse", ""+stream2String(in));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return reussi;
		
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
			
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
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
