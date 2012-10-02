package com.w.authdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
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
    
    private class FetchTokenTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {

        	
	    	HttpClient httpConnection = new DefaultHttpClient();
	    	
	    	HttpGet request = new HttpGet();
	    	
			try {
				URI uri = new URI("http", "authorizr.herokuapp.com", "/api/v1/create_session", "myq", "");
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
				len = 5000;
				System.out.println("reading! bytes " + len);
				byte[] bytes = new byte[len];
				
				int done = inputStream.read(bytes, 0, len);				
				String text = new String(bytes, "UTF8");
				System.out.println(text);
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
    	new FetchTokenTask().execute(null, null);
    	
    	
    	    	
    }
}
