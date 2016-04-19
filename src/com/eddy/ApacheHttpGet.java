package com.eddy;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

public class ApacheHttpGet implements Runnable {

	private String url;
	private String[] headers;
	
	public ApacheHttpGet(String url, String headers) {
		this.url = url;
		this.headers = headers.split(":");
	}

	public void doTask() throws Exception {
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
		
		HttpGet http = new HttpGet(url);
		if (headers != null && headers.length >= 2) {
			for (int i = 0; i < headers.length; i+= 2) {
				http.addHeader(headers[i], headers[i+1]);
			}
		}
		CloseableHttpResponse response = client.execute(http);
		System.out.println(EntityUtils.toString(response.getEntity()));
	}
	
	@Override
	public void run() {
		try {
			doTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
