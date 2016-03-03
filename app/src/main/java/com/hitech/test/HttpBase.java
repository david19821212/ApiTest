package com.hitech.test;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpBase {

	private static final String TAG = "HttpBase";

	private static final int TIMEOUT_CONNECTION = 60000;
	private static final int TIMEOUT_READ = 60000;

	public static byte[] request(String server, String httpParams, int[] statusCode) {

		HttpURLConnection conn = null;

		try {
			conn = connect(server, httpParams, statusCode);
			if (conn == null) {
				return null;
			}

			int contentSize = conn.getContentLength();

			/** not exist content-length field at response-header*/
			if (contentSize < 0) {
				contentSize = 0;
			}

			BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
			byte[] buffer = new byte[contentSize];
			byte[] readBuffer = new byte[1024 * 8];
			int position = 0;
			int readLength = 0;
			for (; true; position += readLength) {
				readLength = inputStream.read(readBuffer, 0, readBuffer.length);

				if (readLength <= 0) {
					break;
				}

				if ((position + readLength) > contentSize) {
					contentSize = position + readLength;
					byte[] newBuffer = new byte[contentSize];
					System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
					buffer = newBuffer;
				}
				System.arraycopy(readBuffer, 0, buffer, position, readLength);
			}

			inputStream.close();
			inputStream = null;

			conn.disconnect();
			conn = null;

			return buffer;

		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (Exception e) {
			Log.e(TAG, "exception", e);
		} catch (Error e) {
			Log.e(TAG, "exception", e);
		}

		if (conn != null) {
			conn.disconnect();
		}

		return null;
	}


	/** http connection */
	protected static HttpURLConnection connect(String server, String params, int[] statusCode) throws IOException {
		URL connectURL = null;

		/** Makes an URL */
		try {
			connectURL = new URL(server);
		} catch (MalformedURLException e) {
			Log.e(TAG, "error", e);
			return null;
		}

		HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
		conn.setConnectTimeout(TIMEOUT_CONNECTION);
		conn.setReadTimeout(TIMEOUT_READ);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "application/json");

		OutputStream os = conn.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write(params);
		writer.flush();
		writer.close();
		os.close();

		int responseCode = conn.getResponseCode();
		Log.e("HttpBase", String.format("%d", responseCode));
		if (statusCode != null) {
			statusCode[0] = responseCode;
		}

		if (responseCode != HttpURLConnection.HTTP_OK) {
			conn.disconnect();
			return null;
		}

		return conn;
	}
}
