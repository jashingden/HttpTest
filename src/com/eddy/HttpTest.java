package com.eddy;

import java.io.FileInputStream;

public class HttpTest {

	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			String url = args[0];
			int num = Integer.parseInt(args[1]);
			String method = "GET";
			if (args.length >= 3) {
				method = args[2];
			}
			if (method.equalsIgnoreCase("GET")) {
				String headers = args.length >= 4 ? args[3] : "";
				try {
					for (int i=0; i<num; i++) {
						new Thread(new ApacheHttpGet(url, headers)).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (method.equalsIgnoreCase("POST")) {
				String postData = "";
				if (args.length >= 4 && args[3].length() > 0) {
					try {
						FileInputStream fs = new FileInputStream(args[3]);
						byte[] b = new byte[fs.available()];
						fs.read(b);
						postData = new String(b, "UTF-8");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String headers = args.length >= 5 ? args[4] : "";
				try {
					for (int i=0; i<num; i++) {
						new Thread(new ApacheHttpConnector(url, postData, headers)).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			usage();
		}
	}
	
	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java -jar HttpTest.jar url count [method] [headers|post_file]");
		System.out.println("\tmethod: GET or POST, default method is GET");
		System.out.println("\tGET: url count GET [headers]");
		System.out.println("\t\theaders: key:value:key:value");
		System.out.println("\tPOST: url count POST [post_file] [headers]");
	}

}
