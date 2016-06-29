package com.eddy;

import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

public class ApacheHttpConnector implements Runnable {
	
	private String url;
	private String postData;
	private String[] headers;
	
	public ApacheHttpConnector(String url, String postData, String headers) {
		this.url = url;
		this.postData = postData;
		this.headers = headers.split(":");
	}
	
	public void postTask() throws Exception {
		CloseableHttpClient client = null;
		if (url.startsWith("https")) {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			});
			SSLContext sslContext = builder.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}
				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}
				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslsf).build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			client = HttpClients.custom().setConnectionManager(cm).build();
		} else {
			client = HttpClients.createDefault();
		}
		
		HttpPost http = new HttpPost(url);
		StringEntity postEntity = new StringEntity(postData);
		http.setEntity(postEntity);
		if (headers != null && headers.length >= 2) {
			for (int i = 0; i < headers.length; i+= 2) {
				http.addHeader(headers[i], headers[i+1]);
			}
		}
		CloseableHttpResponse response = client.execute(http);
		System.out.println(EntityUtils.toString(response.getEntity()));
//		HttpEntity entity = response.getEntity();
//		if (entity != null) {
//			long len = entity.getContentLength();
//			if (len > 0) {
//				System.out.println(EntityUtils.toString(entity));
//			}
//		}
//		response.close();
	}

	@Override
	public void run() {
		try {
			postTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
