package com.ssimwave.challenge;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Represents a manager in the domain model. Managers receive notifications from
 * {@link Publisher}s that create new jobs. A manager sends
 * {@link JobApplication}s to a publisher requesting jobs to complete. A manager
 * has workers that report to them and complete the jobs assigned.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public class Manager implements IJobListener {

	private static final Logger LOGGER = Logger
			.getLogger(Manager.class.getName());

	private static final AtomicInteger MANAGER_ID = new AtomicInteger(1);

	private final int managerID;

	private final int totalWorkerCount;

	/** The worker thread pool that completes the jobs. */
	private final ThreadPoolExecutor workerPool;

	private final AtomicInteger totalCompletedJobs = new AtomicInteger(0);

	/**
	 * The single-thread pool responsible for continually requesting new jobs
	 * from the publisher until all jobs are completed.
	 */
	private final ExecutorService newJobHanderPool;

	public Manager(int numWorkers) {
		this.managerID = MANAGER_ID.getAndIncrement();
		this.totalWorkerCount = numWorkers;
		this.workerPool = new WorkerPoolExecutor(numWorkers);
		this.newJobHanderPool = Executors.newSingleThreadExecutor();
	}

	@Override
	public void newJobsAvailable(Publisher publisher) {
		if (publisher == null)
			return;

		newJobHanderPool.execute(new Runnable() {
			@Override
			public void run() {
				requestJobsFromPublisher(publisher);
			}
		});
	}

	// NOTE: method invoked on newJobHandler thread
	private void requestJobsFromPublisher(Publisher publisher) {
		while (publisher.hasJobsAvailable()) {
			int availableWorkers = totalWorkerCount
					- workerPool.getActiveCount();
			if (availableWorkers > 0) {
				JobApplication jobApplication = new JobApplication(this,
						availableWorkers);
				StringBuilder sb = new StringBuilder();
				sb.append(this).append(" requested up to ")
						.append(availableWorkers).append(" jobs.");
				LOGGER.info(sb.toString());
				publisher.submitJobApplication(jobApplication);
			}
		}
	}

	/**
	 * Invoked by the {@link Publisher} with the list of jobs assigned to
	 * {@link Manager}.
	 * 
	 * @param jobs
	 */
	// NOTE: method invoked on newJobHandler thread
	public void assignJobs(Collection<Job> jobs) {
		StringBuilder sb = new StringBuilder();
		sb.append(this).append(" has been assigned ").append(jobs.size())
				.append(" jobs.");
		LOGGER.info(sb.toString());
		jobs.forEach(job -> workerPool.execute(job));
	}

	// NOTE: method invoked on workerPool thread
	private void jobComplete(Job job) {
		totalCompletedJobs.getAndIncrement();
		LOGGER.info(job + " was completed.");
	}

	public int getTotalCompletedJobs() {
		return totalCompletedJobs.intValue();
	}

	private class WorkerPoolExecutor extends ThreadPoolExecutor {
		public WorkerPoolExecutor(int numWorkers) {
			super(numWorkers, numWorkers, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(), new WorkerFactory());
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			if (r != null && r instanceof Job)
				jobComplete((Job) r);
		}
	}

	@Override
	public String toString() {
		return "Manager-" + managerID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + managerID;
		result = prime * result + totalWorkerCount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Manager other = (Manager) obj;
		if (managerID != other.managerID)
			return false;
		if (totalWorkerCount != other.totalWorkerCount)
			return false;
		return true;
	}

}
