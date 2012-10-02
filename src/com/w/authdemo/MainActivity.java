package com.w.authdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.w.AuthSession;

public class MainActivity extends Activity {

	private AuthSession ses; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private StringBuilder bld;
    private boolean firstArg = false;
    private String sessionid;
    private String server_url;
    private HttpClient httpConnection;
    private String access_token;
    
    public void setArg(String key, String value) {
    	if (!firstArg) {
    		bld.append("&");
    	} else {
    		firstArg = false;
    	}
    	
    	bld.append(key);
    	bld.append("=");
    	try {
			bld.append(URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    private BufferedReader getHttp(String url) throws Exception {
    	HttpGet request = new HttpGet();
		URI uri = new URI(url);
		request.setURI(uri);
		HttpResponse response;
		response = httpConnection.execute(request);
		HttpEntity ent = response.getEntity();
		
		InputStream inputStream = ent.getContent();
			
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		return r;
    }
    
    private class InitAuthTask extends AsyncTask<URL, Integer, Long> {
        

		protected Long doInBackground(URL... urls) {

        	
	    	httpConnection = new DefaultHttpClient();
	    	
	    	HttpGet request = new HttpGet();
	    	server_url = "http://authorizr.herokuapp.com";
	    	bld = new StringBuilder( server_url + "/api/v1/create_session?");
	    	firstArg = true;
	    	setArg("auth_endpoint", "https://accounts.google.com/o/oauth2/auth");
	    	setArg("token_endpoint","https://accounts.google.com/o/oauth2/token");
	    	setArg("resource_endpoint","https://www.googleapis.com/oauth2/v1");
            setArg("redirect_uri",  server_url+"/login/google");
            setArg("scope", "https://www.googleapis.com/auth/drive");
            setArg("cred_id", "100" );
            
            System.out.println("url " + bld.toString());
			try {
				URI uri = new URI(bld.toString());
				request.setURI(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    			
	
			HttpResponse response;
			try {
				response = httpConnection.execute(request);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (long) 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (long) 0;
			}
			try {
				HttpEntity ent = response.getEntity();
				
				InputStream inputStream = ent.getContent();
				
				int len = (int) ent.getContentLength();
				
				BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
				
				Hashtable data = new Hashtable();
				for (;;) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					int sep = line.indexOf("=");
					String k = line.substring(0,sep);
					String v = line.substring(sep+1);
					data.put(k, v);
					System.out.println("Set " + k + " to " + v); 
							
				};
				
				Uri loginuri = Uri.parse((String) data.get("loginurl"));
				
				sessionid = (String) data.get("sessionid");
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, loginuri);
				startActivity(browserIntent);
				
				
				
				
				
				System.out.println("read ok!");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
       }
    }
	
        	

    public void startAuth(View view) {
    	System.out.println("Starting");
    	ses = new AuthSession();
    	ses.startAuth();
    	new InitAuthTask().execute(null, null);
    	
    	
    	    	
    }

    private class FetchTokenTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... params) {
			String access_token_url = server_url + "/api/v1/fetch_access_token/?sessionid="+sessionid;
			try {
				BufferedReader r = getHttp(access_token_url);
				access_token = r.readLine();
				System.out.println("Token is " + access_token); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			// TODO Auto-generated method stub
			return null;
		}
    	
    
    };
    
    private class GetGDriveDataTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... params) {
			 String test_url = "https://www.googleapis.com/drive/v2/files?access_token="+access_token;
			
			 try {
				BufferedReader r = getHttp(test_url);
				for (;;) {
					String line = r.readLine();
					if (line == null)
						break;
					System.out.println(line);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			// TODO Auto-generated method stub
			return null;
		}
    
    }
    public void finishAuth(View view) {
    	new FetchTokenTask().execute(null, null);
		
		
    	
    }
    
    public void doCallApi(View view) {
    	new GetGDriveDataTask().execute(null, null);
    }
    
}
