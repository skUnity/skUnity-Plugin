package com.skunity.plugin.files;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.skunity.plugin.Main;

public class BackupFile {

	private File originalFile;
	private File backedupFile;
	private Boolean isBackedUp;
	public BackupFile(SKUFile file) {
		new BackupFile(file.getFile());
	}
	
	public BackupFile(File file) {
		originalFile = file;
		isBackedUp = false;
	}
	
	public void createBackup() {
		if(!isBackedUp) {
			if(originalFile.exists()) {
				try {
					File maybeFile = new File(Main.backupsFolder.getPath() + "/" + originalFile.getName());
					if(maybeFile.exists()) {
						long dateL = (System.currentTimeMillis() / 1000L);
						maybeFile.renameTo(new File(Main.backupsFolder.getPath() + "/" + dateL + "-" + originalFile.getName()));
					}
					Files.move(originalFile, maybeFile);
					backedupFile = maybeFile;
					isBackedUp = true;
				} catch (IOException e) {
					e.printStackTrace();
					isBackedUp = false;
				}
			}
		}
	}
	
	public void restoreBackup() {
		if(isBackedUp) {
			if(backedupFile.exists()) {
				try {
					Files.move(backedupFile, originalFile);
					isBackedUp = false;
				} catch (IOException e) {
					e.printStackTrace();
					isBackedUp = true;
				}
			}
		}
	}
	
	

}
