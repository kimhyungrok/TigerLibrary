package com.empire.tigerlibrary.tool;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class CommonHandler extends Handler {

	public static interface ICommonHandler {
		public void handleMessage(Message msg);
	}

	private WeakReference<ICommonHandler> mHandler = null;

	public CommonHandler(ICommonHandler handler) {
		mHandler = new WeakReference<ICommonHandler>(handler);
	}

	@Override
	public void handleMessage(Message msg) {
		ICommonHandler oHandler = mHandler.get();
		if (oHandler != null) {
			oHandler.handleMessage(msg);
		}
	}
}
