package com.fhr.concurrentdemo.util;

import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class BaseCasNotSafeConcurrentStack<T> {
	private final AtomicReference<Node<T>> top = new AtomicReference<Node<T>>();

	public void push(Node<T> newTop) {
		Node<T> oldTop = null;
		do {
			oldTop = top.get();
			newTop.next = oldTop;
		} while (top.compareAndSet(oldTop, newTop));
	}

	private Node<T> pop() {
		Node<T> newTop = null;
		Node<T> oldTop = null;
		do {
			oldTop = top.get();
			if (oldTop == null) {
				return null;
			}
			newTop = oldTop.next;
		} while (top.compareAndSet(oldTop, newTop));
		return oldTop;
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
