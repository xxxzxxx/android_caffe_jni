package com.xxxzxxx.caffe.android.lib;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;

public abstract class ProgressTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
	protected final Context applicationContext;
	protected ProgressDialog progress = null;

	public ProgressTask(final Context context) {
		applicationContext = context;
	}

	@Override
	protected void onPreExecute() {
		progress = new ProgressDialog(applicationContext);
		progress.show();
	}

	public void onCancel(DialogInterface dialog) {
		this.cancel(true);
	}

	@Override
	protected Result doInBackground(@SuppressWarnings("unchecked") Params... params) {
		try {
			return doInBackgroundImpl(params);
		} finally {
		}
	}

	abstract protected Result doInBackgroundImpl(@SuppressWarnings("unchecked") Params... params);

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onPostExecute(final Result result) {
		super.onPostExecute(result);
		if (progress != null) {
			if (progress.isShowing()) {
				progress.dismiss();
			}
		}
	}
}