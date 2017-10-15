package com.ssimwave.challenge;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link ThreadFactory} implementation responsible for creating worker
 * threads, owned and managed by the {@link Manager}. This class largely exists
 * so that we can control the name of the worker thread so as to easily identify
 * it when debugging/profiling. It is not absolutely necessary.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
class WorkerFactory implements ThreadFactory {

	private static final AtomicInteger WORKER_ID = new AtomicInteger(1);

	private final ThreadGroup group;

	public WorkerFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup()
				: Thread.currentThread().getThreadGroup();
	}

	/**
	 * Constructs a new {@code Thread}.
	 *
	 * @param runnable
	 *            a runnable to be executed by new thread instance
	 * @return constructed thread, or {@code null} if the request to create a
	 *         thread is rejected (e.g. Runnable is not a {@link Job})
	 */
	@Override
	public Thread newThread(Runnable runnable) {
		Thread worker = new Thread(group, runnable,
				"Worker-" + WORKER_ID.getAndIncrement());

		if (worker.isDaemon())
			worker.setDaemon(false);
		if (worker.getPriority() != Thread.NORM_PRIORITY)
			worker.setPriority(Thread.NORM_PRIORITY);
		return worker;
	}

}
