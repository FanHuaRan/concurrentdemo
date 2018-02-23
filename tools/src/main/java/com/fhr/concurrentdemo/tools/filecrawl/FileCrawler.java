package com.fhr.concurrentdemo.tools.filecrawl;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

public class FileCrawler implements Runnable {
	private final BlockingQueue<File> fileQueue;
	private final FileFilter fileFilter;
	private final File root;

	public FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root) {
		super();
		this.fileQueue = fileQueue;
		this.fileFilter = fileFilter;
		this.root = root;
	}

	@Override
	public void run() {
		try {
			crawl(root);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void crawl(File root) throws InterruptedException {
		File[] entries = root.listFiles(fileFilter);
		if (entries != null) {
			for (File entry : entries) {
				if (entry.isDirectory()) {
					crawl(entry);
				} else if (!alreadyIndexed(entry)) {
					fileQueue.put(entry);
				}
			}
		}
	}

	private boolean alreadyIndexed(File file) {
		return fileQueue.contains(file);
	}
}
