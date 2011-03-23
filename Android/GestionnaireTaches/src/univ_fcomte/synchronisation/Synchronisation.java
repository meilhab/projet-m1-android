package univ_fcomte.synchronisation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

public class Synchronisation {

	public Synchronisation() {
		// TODO Auto-generated constructor stub
	}
	
	
	//=======================================================
	// Recupère une page Web
	//=======================================================
	public String GetHTML(String url, List <NameValuePair> nvps) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse res;
			URI uri = new URI(url);
	    	if (nvps!=null){
	    		HttpPost methodpost = new HttpPost(uri);
	    		methodpost.addHeader("pragma","no-cache");
	    		methodpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	    		res = httpClient.execute(methodpost);
		    } else {
	    		HttpGet methodget = new HttpGet(uri);
		    	methodget.addHeader("pragma","no-cache");
		    	res = httpClient.execute(methodget);
		    }
	    	InputStream data = res.getEntity().getContent();
	    	
	    	return generateString(data);
	    		    	        	
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
	static public String generateString(InputStream stream) {
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
