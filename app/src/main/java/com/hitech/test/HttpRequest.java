package com.hitech.test;

public class HttpRequest {

	HttpRequestListener listener = null;
	String url = null;
	String params = null;
	Object tag = null;

	public HttpRequest(String url, String params, HttpRequestListener listener, Object tag) {
		this.url = url;
		this.params = params;
		this.listener = listener;
		this.tag = tag;
	}

	public static interface HttpRequestListener {

		/** item received data*/
		public Object onHttpReceivedData(byte[] data, Object tag);

		/** item communication completed */
		public void onHttpComplete(int networkStatus, Object data, Object tag);
	}

}
