package com.xxxzxxx.caffe.android.lib;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

/**
 * @author xxxzxxx
 */
public final class CaffeMobile 
{
	static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

	public class CreateInstanceException extends Throwable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5453187170218256563L;
		
	}
	/**
	 * 
	 * @author xxxzxxx
	 *
	 */
	public interface Holder {
		public CaffeMobile getCaffeMobile();
	}

    public static final File kBaseDir = new File(Environment.getExternalStorageDirectory() + "/caffe_mobile");
	public static final File kModelProto = new File(kBaseDir , "deployF.prototxt");
	public static final File kMeanProto = new File(kBaseDir , "/palm.binaryproto");	
	public static final File kModelBinary = new File(kBaseDir , "finetune_7000.caffemodel");
	public static final float[] kMeanValues = {104, 117, 123};
	public static final float kScale = 0.0f;
	public final static String kBlobNames = "prob";

	private long objectAddress = 0;

	/**
	 * finalizerGuardian
	 */
	private final Object finalizerGuardian = new Object()
	{
		protected void finalize()
		{
			if (objectAddress != 0)
			{
				ReleaseCaffeMobile(objectAddress);
			}
		}
	};

	/**
	 * CreateCaffeMobile
	 * @return
	 */
	private static native long CreateCaffeMobile();

	/**
	 * ReleaseCaffeMobile
	 * @param objectAddress
	 */
	private static native void ReleaseCaffeMobile(long objectAddress);

	/**
	 * SetNumThreads
	 * @param numThreads
	 * @return
	 */
	public static native long SetNumThreads(int numThreads);
	/**
	 * LoadModel
	 * @param objectAddress
	 * @param modelPath
	 * @param weightsPath
	 * @return
	 */
	private static native int  LoadModel(long objectAddress,String modelPath, String weightsPath);
	/**
	 * SetMeanWithMeanFile
	 * @param objectAddress
	 * @param meanFile
	 */
	private static native void SetMeanWithMeanFile(long objectAddress,String meanFile);
	private void setMean(File meanFile) {
		SetMeanWithMeanFile(this.objectAddress,meanFile.getPath());
	}
	/**
	 * SetMeanWithMeanValues
	 * @param objectAddress
	 * @param meanValues
	 */
	private static native void SetMeanWithMeanValues(long objectAddress,float[] meanValues);
	private void setMean(float[] meanValues) {
		SetMeanWithMeanValues(this.objectAddress,meanValues);
	}


	/**
	 * setScale
	 * @param objectAddress
	 * @param scale
	 */
	private static native void SetScale(long objectAddress,float scale);

	/**
	 * GetConfidenceScore
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @return
	 */
	private static native float[] GetConfidenceScore(long objectAddress,byte[] buffer, int width, int height);
	private float[] getConfidenceScoreFromPath(String path) {
		return GetConfidenceScore(this.objectAddress,path.getBytes(), 0, 0);
	}
	private float[] getConfidenceScoreFromMat(Mat mat) {
		return GetConfidenceScore(this.objectAddress,mat.getData(), mat.getWidth(), mat.getHeight());
	}

	/**
	 * ExtractFeatures
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @param blobNames
	 * @return
	 */
	private static native float[][] ExtractFeatures(long objectAddress,byte[] buffer, int width, int height, String blobNames);
	private float[][] extractFeaturesFromString(String path, String blobNames) {
		return ExtractFeatures(this.objectAddress,path.getBytes(), 0, 0, blobNames);
	}
	private float[][] extractFeaturesFromMat(Mat mat, String blobNames) {
		return ExtractFeatures(this.objectAddress,mat.getData(), mat.getWidth(), mat.getHeight(), blobNames);
	}

	/**
	 * PredictImage
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @param k
	 * @return
	 */
	private static native int[] PredictImage(long objectAddress,byte[] buffer, int width, int height, int k);
	private int[] predictImageFromPath(final String path) {
		return PredictImage(this.objectAddress,path.getBytes(), 0, 0, 1);
	}
	private int[] predictImageFromMat(final Mat mat) {
		return PredictImage(this.objectAddress,mat.getData(), mat.getWidth(), mat.getHeight(), 1);
	}

	
	/** */
	private final File modelProto;
	/** */
	private final File meanProto;
	/** */
	private final File modelBinary;
	/** */
	private final float scale;
	/** */
	private static CaffeMobile instance = null;
	/** */
	private boolean setuped = false;
	/** */
	private SetupTask setupTask = null;

	/**
	 * @throws CreateInstanceException 
	 * 
	 */
	public CaffeMobile() throws CreateInstanceException
	{
		this.objectAddress = CreateCaffeMobile();
		if (0 == this.objectAddress)
		{
			throw new CreateInstanceException();
		}
		this.modelProto = kModelProto;
		this.modelBinary = kModelBinary;
		this.scale = kScale;
		this.meanProto = kMeanProto;
	}

	public CaffeMobile(final File modelProto, final File modelBinary, final float scale, final File meanProto) throws CreateInstanceException
	{
		this.objectAddress = CreateCaffeMobile();
		if (0 == this.objectAddress)
		{
			throw new CreateInstanceException();
		}
		this.modelProto = modelProto;
		this.modelBinary = modelBinary;
		this.scale = scale;
		this.meanProto = meanProto;
	}

	/**
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanProto
	 * @return
	 * @throws Throwable
	 */
	private static synchronized CaffeMobile createNewInstance(final File modelProto, final File modelBinary, final float scale, final File meanProto) throws Throwable
	{
		if (instance != null)
		{
			throw new Throwable("created static instance.");
		}
		else if(modelProto == null && modelBinary == null && meanProto == null)
		{
			instance = new CaffeMobile();
		}
		else
		{
			instance = new CaffeMobile(modelProto,modelBinary,scale,meanProto);
		}
		return instance;
	}
	
	/**
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanProto
	 * @return
	 * @throws Throwable
	 */
	public static synchronized CaffeMobile createInstance(final File modelProto, final File modelBinary, final float scale, final File meanProto) throws Throwable
	{
		return createNewInstance(modelProto,modelBinary,scale,meanProto);
	}
	/**
	 * 
	 * @return
	 * @throws Throwable
	 */
	public static synchronized CaffeMobile createInstance() throws Throwable
	{
		return createInstance(null,null,kScale,null);
	}
	/**
	 * 
	 * @return
	 */
	public static CaffeMobile getInstance()
	{
		return instance;
	}
	private void internal_setup()
	{
		if (setuped == false)
		{
	        LoadModel(this.objectAddress,modelProto.getPath(), modelBinary.getPath());
	        setMean(meanProto);
	        if (kScale != scale)
	        {
	        	SetScale(this.objectAddress,scale);
	        }
	        setuped = true;
		}
	}
	/**
	 * 
	 */
	public void setup()
	{
		synchronized(this)
		{
			internal_setup();
		}
	}
	/**
	 * 
	 * @param context
	 */
	public void setupAsynctask(Context context)
	{
		if (setuped == false)
		{
			synchronized(setupTask = new SetupTask(context))
			{
				setupTask.execute(this);
			}
		}
	}

	/**
	 * 
	 * @author xxxzxxx
	 */
	private class SetupTask extends ProgressTask<CaffeMobile, String, Void>
	{
		public SetupTask(Context context) {
			super(context);
		}
		protected Void doInBackgroundImpl(CaffeMobile... params)
		{
			for (CaffeMobile c : params)
			{
				if (c.setuped == false)
				{
					publishProgress("setup...");
					c.setup();
				}
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			for (String s : values) {
				progress.setMessage(s);
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setupTask = null;
		}
	}
	
	/**
	 * 
	 * @author xxxzxxx
	 *
	 */
	public class Result implements Serializable{
		private static final long serialVersionUID = 1L;
		private final int executeResult;
		private final Mat target;
		private final float[][] extractFeatures;
		private final float[] extractFeature;

		public Result(final Mat target, final float[][] extractFeatures, final float[] extractFeature,
				final int executeResult) {
			this.executeResult = executeResult;
			this.extractFeatures = extractFeatures;
			this.extractFeature = extractFeature;
			this.target = target;
		}

		public final int getExecuteResult() {
			return executeResult;
		}

		public final Mat getTarget() {
			return target;
		}

		public final float[][] getExtractFeatures() {
			return extractFeatures;
		}

		public final float[] getExtractFeature() {
			return extractFeature;
		}
	}
	private CaffeMobile.Result RunToResult(final Mat target, final String blobNames) {
		float[][] extractFeatures = this.extractFeaturesFromMat(target, blobNames);
		float[] extractFeature = extractFeatures[0];
		int executeResult = this.predictImageFromMat(target)[0];
		return new CaffeMobile.Result(target, extractFeatures, extractFeature, executeResult);
	}

	/**
	 * 
	 * @param target
	 * @return
	 */
	public Result execute(final Mat target) {
		synchronized (this) {
			return RunToResult(target, kBlobNames);
		}
	}
	/**
	 * 
	 * @param target
	 * @param blobNames
	 * @return
	 */
	public Result execute(final Mat target,final String blobNames) {
		synchronized (this) {
			internal_setup();
			return RunToResult(target,blobNames);
		}
	}
	/**
	 * 
	 * @param listner
	 * @param target
	 */
	public void executeAsyncTask(CNNCompletedListener listner, final Mat target) {		
		CNNTask cnnTask = new CNNTask(listner,kBlobNames);
		cnnTask.execute(target);
	}
	/**
	 * 
	 * @param listner
	 * @param target
	 * @param blobNames
	 */
	public void executeAsyncTask(CNNCompletedListener listner, final Mat target,final String blobNames) {
		CNNTask cnnTask = new CNNTask(listner,blobNames);
		cnnTask.execute(target);
	}

	/**
	 * 
	 * @author xxxzxxx
	 *
	 */
	public interface CNNCompletedListener {
		void onTaskCompleted(Result[] result);
	}

	/**
	 * 
	 * @author xxxzxxx
	 *
	 */
	private class CNNTask extends AsyncTask<Mat, Void, Result[]> {
		private final CNNCompletedListener listener;
		private final String blobNames;
		/**
		 * 
		 * @param listener
		 * @param blobNames
		 */
		public CNNTask(CNNCompletedListener listener,final String blobNames) {
			this.listener = listener;
			this.blobNames = blobNames;
		}

		@Override
		protected Result[] doInBackground(Mat... args) {
			ArrayList<Result> results = new ArrayList<Result>();
			for (Mat target : args) {
				results.add(CaffeMobile.this.execute(target,blobNames));
			}
			return results.toArray(new Result[0]);
		}

		@Override
		protected void onPostExecute(Result[] result) {
			listener.onTaskCompleted(result);
			super.onPostExecute(result);
		}
	}
}
