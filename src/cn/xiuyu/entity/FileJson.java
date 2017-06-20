package cn.xiuyu.entity;

public class FileJson {
	//以上传
	private long pBytesRead;
	//文件大小
	private long pContentLength;
	public long getpBytesRead() {
		return pBytesRead;
	}
	public void setpBytesRead(long pBytesRead) {
		this.pBytesRead = pBytesRead;
	}
	public long getpContentLength() {
		return pContentLength;
	}
	public void setpContentLength(long pContentLength) {
		this.pContentLength = pContentLength;
	}
	public FileJson(long pBytesRead, long pContentLength) {
		super();
		this.pBytesRead = pBytesRead;
		this.pContentLength = pContentLength;
	}
	public FileJson() {
		super();
	}
	
}
