package cn.xiuyu.entity;

import java.io.File;

public class FileEntity {
	private String name;
	private String type;
	private String path;
	private String newName;
	private File file;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public FileEntity(String name, String type, String path, String newName, File file) {
		super();
		this.name = name;
		this.type = type;
		this.path = path;
		this.newName = newName;
		this.file = file;
	}
	public FileEntity() {
		super();
	}
	
}
