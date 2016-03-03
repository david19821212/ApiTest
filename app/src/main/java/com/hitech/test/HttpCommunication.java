package com.hitech.test;

import android.os.AsyncTask;
import android.util.Log;

public class HttpCommunication {

	private static final String TAG = "HttpCommunication";

	public static void sendRequest(HttpRequest request) {
		try {
			CommunicationTask task = new CommunicationTask(request);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (Exception e) {
			Log.e(TAG, "error", e);
		}
	}

	/** thread inner class */
	private static class CommunicationTask extends AsyncTask<Object, Object, Object> {

		private HttpRequest mHttpRequest;
		private int mStatusCode;

		public CommunicationTask(HttpRequest httpItem) {
			mHttpRequest = httpItem;
		}

		@Override
		protected Object doInBackground(Object... params) {
			int[] statusCode = {0};
			byte[] data = HttpBase.request(mHttpRequest.url, mHttpRequest.params, statusCode);

			Object result;
			if (mHttpRequest.listener != null) {
				result = mHttpRequest.listener.onHttpReceivedData(data, mHttpRequest.tag);
			} else {
				result = data;
			}

			mStatusCode = statusCode[0];

			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mHttpRequest.listener != null) {
				mHttpRequest.listener.onHttpComplete(mStatusCode, result, mHttpRequest.tag);
			}
		}
	}

	public static interface HttpCommListener {
		public void onNetworkResult(boolean success, String data);
	}
}
