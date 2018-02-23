package com.fhr.concurrentdemo.tools.filecrawl;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fhr.concurrentdemo.tools.filecrawl.Indexer.Indexable;

public class FileCrawlerBoot {
	private static final int N_CONSUMERS = 5;

	public static void startIndex(File[] roots, Indexable index) {
		if (roots != null) {
			BlockingQueue<File> queue = new LinkedBlockingQueue<>();
			FileFilter fileFilter = p -> true;

			for (File root : roots) {
				new Thread(new FileCrawler(queue, fileFilter, root)).start();
			}

			for (int i = 0; i < N_CONSUMERS; i++) {
				new Thread(new Indexer(queue, index)).start();
			}
		}
	}
}
