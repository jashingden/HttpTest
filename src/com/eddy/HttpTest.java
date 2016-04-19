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
				String postData = "FUN=7003|>SNO=1111|>AC=1234567|>BID=779|>PWD=2222|>PERSONALID=A210254862|>CLIENTIP=127.0.0.1|>TIME=20150608000000|>ORG=GPHONE|>";
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
	//			String postData = "{\n"+
	//		            "    \"appkey\":\"hYy00NDhmLTgyYTItN\",\n"+
	//		            "    \"bid\":\"00015\",\n"+
	//		            "    \"platform\":\"AndroidPhone\",\n"+
	//		            "    \"brand\":\"Xiaomi\",\n"+
	//		            "    \"device\":\"MI 3W\",\n"+
	//		            "    \"os\":\"19\",\n"+
	//		            "    \"hid\":\"06338bede480983e\",\n"+
	//		            "    \"name\":\"com.sseinfo.xcsc\",\n"+
	//		            "    \"ver\":\"1.0.0\",\n"+
	//		            "    \"timestamp\":\"1438678456957\"\n"+
	//		            "}";
				
				try {
					for (int i=0; i<num; i++) {
						new Thread(new ApacheHttpConnector(url, postData)).start();
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
		System.out.println("\tPOST: url count POST [post_file]");
	}

}
