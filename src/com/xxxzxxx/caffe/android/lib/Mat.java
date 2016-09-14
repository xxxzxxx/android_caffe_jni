package com.xxxzxxx.caffe.android.lib;


import java.io.Serializable;

/**
 * Created by xxx on 16/09/02.
 */
public class Mat implements Serializable {
	private static final long serialVersionUID = -1L;
	private final int channel;
	private final int height;
	private final int width;
	private final byte[] data;

	public Mat(final int width, final int height, final int channel) {
		try {
			this.width = width;
			this.height = height;
			this.channel = channel;
			this.data = new byte[width * height * channel];
		} finally {
		}
	}

	public Mat(final int width, final int height, final int channel, final byte[] data) {
		try {
			this.width = width;
			this.height = height;
			this.channel = channel;
			if (data == null) {
				throw new RuntimeException("argument data is null.");
			} else if (data.length != (width * height * channel)) {
				throw new RuntimeException("argument data length is loss.");
			}
			this.data = data.clone();
		} finally {
		}
	}

	public int getChannel() {
		return channel;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	};

	public byte[] getData() {
		return data;
	}

	public int getDataArraySize() {
		return data.length;
	}

	public boolean equealHeader(final int width, final int height, final int channel, boolean isthrow) {
		try {
			boolean result = true;
			if (width != this.width) {
				if (isthrow) {
					throw new RuntimeException("mat.width != this.width");
				}
				result = false;
			} else if (height != this.height) {
				if (isthrow) {
					throw new RuntimeException("mat.height != this.height");
				}
				result = false;
			} else if (channel != this.channel) {
				if (isthrow) {
					throw new RuntimeException("mat.channel != this.channel");
				}
				result = false;
			}
			return result;
		} finally {
		}

	}

	public boolean equealHeader(final Mat mat, boolean isthrow) {
		try {
			boolean result = true;
			if (mat.width != this.width) {
				if (isthrow) {
					throw new RuntimeException("mat.width != this.width");
				}
				result = false;
			} else if (mat.height != this.height) {
				if (isthrow) {
					throw new RuntimeException("mat.height != this.height");
				}
				result = false;
			} else if (mat.channel != this.channel) {
				if (isthrow) {
					throw new RuntimeException("mat.channel != this.channel");
				}
				result = false;
			}
			return result;
		} finally {
		}

	}

	public boolean isInvalid(boolean isthrow) {
		try {
			boolean result = false;
			if (data == null) {
				if (isthrow) {
					throw new RuntimeException("parameter is null instance.");
				}
				result = true;
			} else if (data.length != (width * height * channel)) {
				if (isthrow) {
					throw new RuntimeException("parameter is not request size.");
				}
				result = true;
			}
			return result;
		} finally {
		}

	}
}
