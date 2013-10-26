package com.miiitv.game.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.support.v4.net.TrafficStatsCompat;

import com.miiitv.game.client.Logger;
import com.miiitv.game.client.config.Config;
import com.miiitv.game.network.NetworkException.TYPE;


public class Network {
	
	private final static String TAG = "Network";
	private static Network instance = null;
	private DefaultHttpClient connection;
	
	private Network() {
		init();
	}
	
	private void init() {
		Logger.d(TAG, "Network init");
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, Config.DEFAULT_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, Config.DEFAULT_SOCKET_TIMEOUT);
		ConnManagerParams.setMaxTotalConnections(params, 30);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(15));
		if (Config.PROXY) {
			HttpHost proxy = new HttpHost(Config.PROXY_IP, Config.PROXY_PORT);
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		connection = new DefaultHttpClient(cm , params);
		connection.setCookieStore(new BasicCookieStore());
		
		if (Logger.isDebug()) TrafficStatsCompat.setThreadStatsTag(0xF00D);
	}
	
	public static Network getInstance() {
		if (instance == null)
			instance = new Network();
		return instance;
	}
	
	public void clearCookie() {
		connection.getCookieStore().clear();
	}
	
	public CookieStore getCookieStore() {
		return connection.getCookieStore();
	}
	
	public CookieSpecRegistry getCookieSpecs() {
		return connection.getCookieSpecs();
	}
	
	public String post(String action, ArrayList<NameValuePair> postValues) throws NetworkException {
		Logger.d(TAG, "Post: ", action, " data: ", postValues.toString());
		String data = null;
		String url = Config.API_URL + action;
		Logger.d(TAG, "Url: ", url);
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(postValues, HTTP.UTF_8));
			HttpResponse response = connection.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				new NetworkException(TYPE.HTTP_FAIL);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				data = EntityUtils.toString(entity, HTTP.UTF_8);
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public String get(String action, ArrayList<NameValuePair> getValues) throws NetworkException {
		if (getValues == null) {
			Logger.d(TAG, "Get: ", action);
		} else {
			Logger.d(TAG, "Get: ", action, " data: ", getValues.toString());
		}
		String data = null;
		StringBuilder url = new StringBuilder();
		url.append(Config.API_URL + action);
		if (getValues != null)
			url.append("?" + URLEncodedUtils.format(getValues, "UTF-8").toString());
		Logger.d(TAG, "Url: ", url.toString());
		HttpGet get = new HttpGet(url.toString());
		try {
			HttpResponse response = connection.execute(get);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) new NetworkException(TYPE.HTTP_FAIL);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				data = EntityUtils.toString(entity, HTTP.UTF_8);
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void NetworkClose() {
		Logger.i(TAG, "NetworkClose()");
		connection.clearRequestInterceptors();
		connection.clearResponseInterceptors();
		connection.getConnectionManager().shutdown();
		instance = null;
	}
	
	public long postDownload(String action, ArrayList<NameValuePair> postValue, String filename, String savePtah) {
		Logger.d(TAG, "Download post data: ", postValue.toString());
		long size = 0;
		String url = Config.API_URL + action;
		Logger.d(TAG, "Url: ", url);
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(postValue, HTTP.UTF_8));
			HttpResponse response = connection.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new NetworkException(TYPE.HTTP_FAIL);
			byte[] buffer = new byte[1024 * 8];
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				File path = new File(savePtah);
				if ( ! path.exists())
					path.mkdirs();
				File savePathFilename = new File(path, filename);
				Logger.e(TAG, "downlad save path: ", savePathFilename.getAbsolutePath());
				FileOutputStream os = new FileOutputStream(savePathFilename);
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.flush();
				os.close();
				size = entity.getContentLength();
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NetworkException e) {
			e.printStackTrace();
		}
		return size;
	}
	
	public long download(String url, String filename, String savePtah) {
		Logger.d(TAG, "Download url: ", url);
		long size = 0;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = connection.execute(get);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new NetworkException(TYPE.HTTP_FAIL);
			byte[] buffer = new byte[1024 * 8];
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				File path = new File(savePtah);
				if ( ! path.exists())
					path.mkdirs();
				File savePathFilename = new File(path, filename);
				Logger.e(TAG, "Image downlad save path: ", savePathFilename.getAbsolutePath());
				FileOutputStream os = new FileOutputStream(savePathFilename);
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.flush();
				os.close();
				size = entity.getContentLength();
				entity.consumeContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NetworkException e) {
			e.printStackTrace();
		}
		return size;
	}
}
