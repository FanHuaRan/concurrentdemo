package com.fhr.concurrentdemo.tools.filecrawl;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class Indexer implements Runnable {
	private final BlockingQueue<File> blockingQueue;
	private final Indexable indexable;

	public Indexer(BlockingQueue<File> blockingQueue, Indexable indexable) {
		super();
		this.blockingQueue = blockingQueue;
		this.indexable = indexable;
	}

	@Override
	public void run() {
		try {
			while (true) {
				indexable.index(blockingQueue.take());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

	}

	@FunctionalInterface
	public static interface Indexable {
		void index(File file);
	}
}
