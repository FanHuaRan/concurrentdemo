package com.fhr.concurrentdemo.util;

import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class BaseCasConcurrentStack<T> {
	private final AtomicReference<Node<T>> top = new AtomicReference<Node<T>>();

	public void push(T value) {
		Node<T> newTop = new Node<T>(value);
		Node<T> oldTop = null;
		do {
			oldTop = top.get();
			newTop.next = oldTop;
		} while (top.compareAndSet(oldTop, newTop));
	}

	private T pop() {
		Node<T> newTop = null;
		Node<T> oldTop = null;
		do {
			oldTop = top.get();
			if (oldTop == null) {
				return null;
			}
			newTop = oldTop.next;
		} while (top.compareAndSet(oldTop, newTop));
		return oldTop.value;
	}

	private static class Node<T> {
		public final T value;
		public Node next;

		public Node(T value) {
			super();
			this.value = value;
		}

	}
}
