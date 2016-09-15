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
public final class CaffeMobile {
	static {
		System.loadLibrary("caffe");
		System.loadLibrary("caffe_jni");
	}

	public class CreateInstanceException extends Throwable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5453187170218256563L;

	}

	public class LostInstanceException extends Throwable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1019284859621221287L;
	}

	/**
	 * 
	 * @author xxxzxxx
	 *
	 */
	public interface Holder {
		public CaffeMobile getCaffeMobile();
	}

	/*
	 * public static final File kBaseDir = new
	 * File(Environment.getExternalStorageDirectory() + "/caffe_mobile"); public
	 * static final File kModelProto = new File(kBaseDir , "deployF.prototxt");
	 * public static final File kMeanProto = new File(kBaseDir ,
	 * "/palm.binaryproto"); public static final File kModelBinary = new
	 * File(kBaseDir , "finetune_7000.caffemodel"); public static final float[]
	 * kMeanValues = {104, 117, 123}; public static final float kScale = 0.0f;
	 * public final static String kBlobNames = "prob";
	 */
	public static final String kModelProtoName = "deploy.prototxt";
	public static final String kModelBinaryName = "bvlc_reference_caffenet.caffemodel";
	public static final File kBaseDir = new File(
			Environment.getExternalStorageDirectory() + "/caffe_mobile/bvlc_reference_caffenet");
	public static final File kModelProto = new File(kBaseDir, kModelProtoName);
	public static final File kModelBinary = new File(kBaseDir, kModelBinaryName);
	public static final float[] kMeanValues = { 104, 117, 123 };
	public static final float kScale = 0.0f;
	public final static String kBlobNames = "prob";

	private long objectAddress = 0;

	/**
	 * finalizerGuardian
	 */
	private final Object finalizerGuardian = new Object() {
		protected void finalize() {
			if (objectAddress != 0) {
				ReleaseCaffeMobile(objectAddress);
			}
		}
	};

	/**
	 * CreateCaffeMobile
	 * 
	 * @return
	 */
	private static native long CreateCaffeMobile();

	/**
	 * ReleaseCaffeMobile
	 * 
	 * @param objectAddress
	 */
	private static native void ReleaseCaffeMobile(long objectAddress);

	/**
	 * SetNumThreads
	 * 
	 * @param numThreads
	 * @return
	 */
	public static native long SetNumThreads(int numThreads);

	/**
	 * LoadModel
	 * 
	 * @param objectAddress
	 * @param modelPath
	 * @param weightsPath
	 * @return
	 */
	private static native int LoadModel(long objectAddress, String modelPath, String weightsPath);

	/**
	 * SetMeanWithMeanFile
	 * 
	 * @param objectAddress
	 * @param meanFile
	 */
	private static native int SetMeanWithMeanFile(long objectAddress, String meanFile);

	private void setMean(File meanFile) {
		SetMeanWithMeanFile(this.objectAddress, meanFile.getPath());
	}

	/**
	 * SetMeanWithMeanValues
	 * 
	 * @param objectAddress
	 * @param meanValues
	 */
	private static native int SetMeanWithMeanValues(long objectAddress, float[] meanValues);

	private void setMean(float[] meanValues) {
		SetMeanWithMeanValues(this.objectAddress, meanValues);
	}

	/**
	 * setScale
	 * 
	 * @param objectAddress
	 * @param scale
	 */
	private static native int SetScale(long objectAddress, float scale);

	/**
	 * GetConfidenceScore
	 * 
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @return
	 */
	private static native float[] GetConfidenceScore(long objectAddress, byte[] buffer, int width, int height);

	private float[] getConfidenceScoreFromPath(String path) throws LostInstanceException {
		final float[] result = GetConfidenceScore(this.objectAddress, path.getBytes(), 0, 0);
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	private float[] getConfidenceScoreFromMat(Mat mat) throws LostInstanceException {
		final float[] result = GetConfidenceScore(this.objectAddress, mat.getData(), mat.getWidth(), mat.getHeight());
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	/**
	 * ExtractFeatures
	 * 
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @param blobNames
	 * @return
	 */
	private static native float[][] ExtractFeatures(long objectAddress, byte[] buffer, int width, int height,
			String blobNames);

	private float[][] extractFeatures(Object target, String blobNames) throws LostInstanceException {
		if (target instanceof File) {
			return extractFeatures((File) target, blobNames);
		} else if (target instanceof Mat) {
			return extractFeatures((Mat) target, blobNames);
		} else {
			throw new RuntimeException("target is unknown instance type.");
		}
	}

	private float[][] extractFeatures(File target, String blobNames) throws LostInstanceException {
		if (!target.exists()) {
			throw new RuntimeException("target file is not found.");
		} else if (target.isDirectory()) {
			throw new RuntimeException("target is directory.");
		}
		final float[][] result = ExtractFeatures(this.objectAddress, target.getPath().getBytes(), 0, 0, blobNames);
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	private float[][] extractFeatures(Mat mat, String blobNames) throws LostInstanceException {
		final float[][] result = ExtractFeatures(this.objectAddress, mat.getData(), mat.getWidth(), mat.getHeight(),
				blobNames);
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	/**
	 * PredictImage
	 * 
	 * @param objectAddress
	 * @param buffer
	 * @param width
	 * @param height
	 * @param k
	 * @return
	 */
	private static native int[] PredictImage(long objectAddress, byte[] buffer, int width, int height, int k);

	private int[] predictImage(Object target) throws LostInstanceException {
		if (target instanceof File) {
			return predictImage((File) target);
		} else if (target instanceof Mat) {
			return predictImage((Mat) target);
		} else {
			throw new RuntimeException("target is unknown instance type.");
		}
	}

	private int[] predictImage(final File target) throws LostInstanceException {
		if (!target.exists()) {
			throw new RuntimeException("target file is not found.");
		} else if (target.isDirectory()) {
			throw new RuntimeException("target is directory.");
		}

		final int[] result = PredictImage(this.objectAddress, target.getPath().getBytes(), 0, 0, 1);
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	private int[] predictImage(final Mat mat) throws LostInstanceException {
		final int[] result = PredictImage(this.objectAddress, mat.getData(), mat.getWidth(), mat.getHeight(), 1);
		if (result == null) {
			throw new LostInstanceException();
		}
		return result;
	}

	/** */
	private final File modelProto;
	/** */
	private final File meanProto;
	/** */
	private final float[] meanValues;
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
	 */
	public CaffeMobile() throws CreateInstanceException {
		this.objectAddress = CreateCaffeMobile();
		if (0 == this.objectAddress) {
			throw new CreateInstanceException();
		}
		this.modelProto = kModelProto;
		this.modelBinary = kModelBinary;
		this.scale = kScale;
		this.meanProto = null;
		this.meanValues = kMeanValues;
	}

	/**
	 * @throws CreateInstanceException
	 */
	public CaffeMobile(final File baseDir) throws CreateInstanceException {
		this.objectAddress = CreateCaffeMobile();
		if (!baseDir.isDirectory()) {
			throw new RuntimeException("baseDir is not directory");
		}
		if (0 == this.objectAddress) {
			throw new CreateInstanceException();
		}
		this.scale = kScale;
		this.meanProto = null;
		this.meanValues = kMeanValues;
		this.modelProto = new File(baseDir, kModelProtoName);
		if (!modelProto.exists()) {
			throw new RuntimeException("modelProto is not exists");
		} else if (modelProto.isDirectory()) {
			throw new RuntimeException("modelProto is directory");
		}
		this.modelBinary = new File(baseDir, kModelBinaryName);
		if (!modelBinary.exists()) {
			throw new RuntimeException("modelBinary is not exists");
		} else if (modelBinary.isDirectory()) {
			throw new RuntimeException("modelBinary is directory");
		}
	}

	/**
	 * CaffeMobile initilization
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanValues
	 * @throws CreateInstanceException
	 */
	public CaffeMobile(final File modelProto, final File modelBinary, final float scale, final float[] meanValues)
			throws CreateInstanceException {
		this.objectAddress = CreateCaffeMobile();
		if (0 == this.objectAddress) {
			throw new CreateInstanceException();
		}
		if (modelProto.isDirectory()) {
			throw new RuntimeException("modelProto is directory!");
		}
		if (modelBinary.isDirectory()) {
			throw new RuntimeException("modelBinary is directory!");
		}
		this.modelProto = modelProto;
		this.modelBinary = modelBinary;
		this.scale = scale;
		this.meanValues = meanValues;
		this.meanProto = null;
	}

	/**
	 * CaffeMobile initilization
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanProto
	 * @throws CreateInstanceException
	 */
	public CaffeMobile(final File modelProto, final File modelBinary, final float scale, final File meanProto)
			throws CreateInstanceException {
		this.objectAddress = CreateCaffeMobile();
		if (0 == this.objectAddress) {
			throw new CreateInstanceException();
		}
		if (modelProto.isDirectory()) {
			throw new RuntimeException("modelProto is directory!");
		}
		if (modelBinary.isDirectory()) {
			throw new RuntimeException("modelBinary is directory!");
		}
		if (meanProto.isDirectory()) {
			throw new RuntimeException("meanProto is directory!");
		}
		this.modelProto = modelProto;
		this.modelBinary = modelBinary;
		this.scale = scale;
		this.meanValues = null;
		this.meanProto = meanProto;
	}

	/**
	 * createNewInstance
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanProto
	 * @return
	 * @throws Throwable
	 */
	private static synchronized CaffeMobile createNewInstance(final File modelProto, final File modelBinary,
			final float scale, final File meanProto, final float[] meanValues) throws Throwable {
		if (instance != null) {
			throw new Throwable("created static instance.");
		} else if (modelProto == null && modelBinary == null && meanProto == null) {
			instance = new CaffeMobile();
		} else {
			if (meanValues == null && meanProto == null) {
				throw new Throwable("mean values is null!!");
			} else if (meanValues == null) {
				instance = new CaffeMobile(modelProto, modelBinary, scale, meanProto);
			} else {
				instance = new CaffeMobile(modelProto, modelBinary, scale, meanValues);
			}
		}
		return instance;
	}

	/**
	 * createInstance
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanProto
	 * @return
	 * @throws Throwable
	 */
	public static CaffeMobile createInstance(final File modelProto, final File modelBinary, final float scale,
			final File meanProto) throws Throwable {
		return createNewInstance(modelProto, modelBinary, scale, meanProto, null);
	}

	/**
	 * createInstance
	 * 
	 * @param modelProto
	 * @param modelBinary
	 * @param scale
	 * @param meanValues
	 * @return
	 * @throws Throwable
	 */
	public static CaffeMobile createInstance(final File modelProto, final File modelBinary, final float scale,
			final float[] meanValues) throws Throwable {
		return createNewInstance(modelProto, modelBinary, scale, null, meanValues);
	}

	/**
	 * createInstance
	 * 
	 * @return
	 * @throws Throwable
	 */
	public static CaffeMobile createInstance() throws Throwable {
		return createNewInstance(null, null, 0f, null, null);
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static CaffeMobile getInstance() {
		return instance;
	}

	private void internal_setup() {
		if (setuped == false) {
			LoadModel(this.objectAddress, modelProto.getPath(), modelBinary.getPath());
			if (meanProto != null) {
				setMean(meanProto);
			} else if (meanValues != null) {
				setMean(meanValues);
			}
			if (kScale != scale) {
				SetScale(this.objectAddress, scale);
			}
			setuped = true;
		}
	}

	/**
	 * setup
	 */
	public void setup() {
		synchronized (this) {
			internal_setup();
		}
	}

	/**
	 * setupAsynctask
	 * 
	 * @param context
	 */
	public void setupAsynctask(Context context) {
		if (setuped == false) {
			synchronized (setupTask = new SetupTask(context)) {
				setupTask.execute(this);
			}
		}
	}

	/**
	 * 
	 * @author xxxzxxx
	 */
	private class SetupTask extends ProgressTask<CaffeMobile, String, Void> {
		public SetupTask(Context context) {
			super(context);
		}

		protected Void doInBackgroundImpl(CaffeMobile... params) {
			for (CaffeMobile c : params) {
				if (c.setuped == false) {
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
	public class Result implements Serializable {
		private static final long serialVersionUID = -6328689008287050327L;
		private final Object target;
		private final int[] executeResults;
		private final float[][] extractFeatures;
		private final float[] extractFeature;
		private final Throwable exception;
		private final boolean executeExtractFeatures;
		private final boolean executePredictImage;

		Result(final Object target, final boolean executeExtractFeatures, final boolean executePredictImage,
				final float[][] extractFeatures, final float[] extractFeature, final int[] executeResults,
				final Throwable exception) {
			this.executeExtractFeatures = executeExtractFeatures;
			this.executePredictImage = executePredictImage;
			this.executeResults = executeResults;
			this.extractFeatures = extractFeatures;
			this.extractFeature = extractFeature;
			this.target = target;
			this.exception = exception;
		}

		public final boolean isExecuteExtractFeatures() {
			return executeExtractFeatures;
		}

		public final boolean isExecutePredictImage() {
			return executePredictImage;
		}

		public final int getExecuteResult() {
			return executeResults != null ? executeResults[0] : -1;
		}

		public final int[] getExecuteResults() {
			return executeResults;
		}

		public final Object getTarget() {
			return target;
		}

		public final float[][] getExtractFeatures() {
			return extractFeatures;
		}

		public final float[] getExtractFeature() {
			return extractFeature;
		}

		public final Throwable getException() {
			return exception;
		}

		public final boolean isFailed() {
			return (exception != null);
		}
	}

	/**
	 * RunPredictImageAndExtratFeatures
	 * 
	 * @param target
	 * @param blobNames
	 * @param executeExtractFeatures
	 * @param executePredictImage
	 * @return
	 */
	private CaffeMobile.Result RunPredictImageAndExtratFeatures(final Object target, final String blobNames,
			final boolean executeExtractFeatures, final boolean executePredictImage) {
		float[][] extractFeatures = null;
		float[] extractFeature = null;
		int[] executeResult = null;
		Throwable exception = null;
		try {
			if (executeExtractFeatures) {
				if (blobNames == null) {
					throw new NullPointerException("blobNames is null!!");
				}
				extractFeatures = this.extractFeatures(target, blobNames);
				extractFeature = extractFeatures[0];
			}
			if (executePredictImage) {
				executeResult = this.predictImage(target);
			}
		} catch (final Throwable ex) {
			ex.printStackTrace();
			exception = ex;
		}
		return new CaffeMobile.Result(target, executeExtractFeatures, executePredictImage, extractFeatures,
				extractFeature, executeResult, exception);
	}

	/**
	 * execute
	 * 
	 * @param target
	 * @param blobNames
	 * @param executeExtractFeatures
	 * @param executePredictImage
	 * @return
	 */
	private Result execute(final Object target, final String blobNames, final boolean executeExtractFeatures,
			final boolean executePredictImage) {
		synchronized (this) {
			internal_setup();
			return RunPredictImageAndExtratFeatures(target, blobNames, executeExtractFeatures, executePredictImage);
		}
	}

	/**
	 * execute
	 * 
	 * @param target
	 * @param blobNames
	 * @param executeExtractFeatures
	 * @param executePredictImage
	 * @return
	 */
	private Result execute(final File target, final String blobNames, final boolean executeExtractFeatures,
			final boolean executePredictImage) {
		synchronized (this) {
			internal_setup();
			return RunPredictImageAndExtratFeatures(target, blobNames, executeExtractFeatures, executePredictImage);
		}
	}

	/**
	 * executePredictImage
	 * 
	 * @param target
	 * @return
	 * @throws LostInstanceException
	 */
	public Result executePredictImage(final File target) {
		return execute(target, null, false, true);
	}

	/**
	 * executePredictImage
	 * 
	 * @param target
	 * @return
	 * @throws LostInstanceException
	 */
	public Result executePredictImage(final Mat target) {
		return execute(target, null, false, true);
	}

	/**
	 * executeExtractFeatures
	 * 
	 * @param target
	 * @return
	 * @throws LostInstanceException
	 */
	public Result executeExtractFeatures(final File target, final String blobNames) {
		return execute(target, blobNames, true, false);
	}

	/**
	 * executeExtractFeatures
	 * 
	 * @param target
	 * @return
	 * @throws LostInstanceException
	 */
	public Result executeExtractFeatures(final Mat target, final String blobNames) {
		return execute(target, blobNames, true, false);
	}

	/**
	 * 
	 * @param target
	 * @param blobNames
	 * @return
	 */
	public Result executePredictImageWithExtractFeatures(final File target, final String blobNames) {
		return execute(target, blobNames, true, true);
	}

	/**
	 * executePredictImageWithExtractFeatures
	 * 
	 * @param target
	 * @param blobNames
	 * @return
	 */
	public Result executePredictImageWithExtractFeatures(final Mat target, final String blobNames) {
		return execute(target, blobNames, true, true);
	}

	/**
	 * executePredictImageAsyncTask
	 * 
	 * @param target
	 * @param listner
	 */
	public void executePredictImageAsyncTask(final File target, final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, null, false, true);
		cnnTask.execute(target);
	}

	public void executePredictImageAsyncTask(final Mat target, final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, null, false, true);
		cnnTask.execute(target);
	}

	/**
	 * 
	 * @param target
	 * @param blobNames
	 * @param listner
	 */
	public void executeExtractFeaturesAsyncTask(final File target, final String blobNames,
			final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, blobNames, true, false);
		cnnTask.execute(target);
	}

	public void executeExtractFeaturesAsyncTask(final Mat target, final String blobNames,
			final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, blobNames, true, false);
		cnnTask.execute(target);
	}

	/**
	 * 
	 * @param target
	 * @param blobNames
	 * @param listner
	 */
	public void executePredictImageWithExtractFeaturesAsyncTask(final File target, final String blobNames,
			final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, blobNames, true, true);
		cnnTask.execute(target);
	}

	public void executePredictImageWithExtractFeaturesAsyncTask(final Mat target, final String blobNames,
			final CNNCompletedListener listner) {
		CNNTask cnnTask = new CNNTask(listner, blobNames, true, true);
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
	private class CNNTask extends AsyncTask<Object, Void, Result[]> {
		private final CNNCompletedListener listener;
		private final String blobNames;
		private final boolean executePredictImage;
		private final boolean executeExtractFeatures;

		/**
		 * 
		 * @param listener
		 * @param blobNames
		 */
		public CNNTask(CNNCompletedListener listener, final String blobNames, final boolean executeExtractFeatures,
				final boolean executePredictImage) {
			this.listener = listener;
			this.blobNames = blobNames;
			this.executeExtractFeatures = executeExtractFeatures;
			this.executePredictImage = executePredictImage;
		}

		@Override
		protected Result[] doInBackground(Object... args) {
			ArrayList<Result> results = new ArrayList<Result>();
			for (Object target : args) {
				results.add(CaffeMobile.this.execute(target, this.blobNames, this.executeExtractFeatures,
						this.executePredictImage));
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
