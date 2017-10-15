package com.ssimwave.challenge;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The publisher is responsible for listening to the console input (i.e. user)
 * and creating the required number of jobs. The publisher notifies each
 * registered {@link IJobListener} once new jobs have been added and assigns
 * jobs to job applicants.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public class Publisher {

	private static final Logger LOGGER = Logger
			.getLogger(Publisher.class.getName());

	private final ConcurrentLinkedQueue<Job> availableJobs;

	/** Use a set for the listeners to prevent (unintentional) duplicates. */
	private final HashSet<IJobListener> jobListeners;

	/** Pool used to notify registered listeners of new jobs. */
	private final ExecutorService jobNotifierPool;

	public Publisher() {
		this.availableJobs = new ConcurrentLinkedQueue<Job>();
		this.jobListeners = new HashSet<IJobListener>();
		this.jobNotifierPool = Executors.newCachedThreadPool();
	}

	/** Exception used to indicate that the Console is not available. */
	public class MissingConsoleException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Tells the {@link Publisher} to start listening to the console for user
	 * input.
	 * 
	 * @throws MissingConsoleException
	 *             thrown if the {@link Console} is not available.
	 */
	public void start() throws MissingConsoleException {
		Console console = System.console();
		if (console == null)
			throw new MissingConsoleException();

		while (true) {
			StringBuilder sb = new StringBuilder();
			sb.append(
					"Enter the number of jobs you wish to create and hit <Enter>.\n")
					.append("You may add jobs at any time, even as console is scrolling.\n")
					.append("Type \"quit\" and hit <Enter> to terminate program and view stats.\n");
			console.printf(sb.toString());
			if (console != null) {
				String jobsToCreate = console.readLine();
				if ("quit".equals(jobsToCreate.toLowerCase())) {
					synchronized (jobListeners) {
						jobListeners.forEach(jobListener -> console
								.printf(jobListener + " completed "
										+ ((Manager) jobListener)
												.getTotalCompletedJobs()
										+ " jobs.\n"));
						System.exit(0);
					}
				}
				try {
					Integer numJobs = Integer.parseInt(jobsToCreate);
					createJobs(numJobs);
				} catch (NumberFormatException e) {
					LOGGER.info("Unknown input.");
				}
			}
		}
	}

	private boolean createJobs(int numJobs) {
		if (numJobs <= 0)
			return false;

		LOGGER.info("Creating " + numJobs + " new jobs.");
		for (int i = 0; i < numJobs; i++) {
			availableJobs.add(new Job());
		}

		notifyJobListenersOfNewJobs(numJobs);
		return true;
	}

	private void notifyJobListenersOfNewJobs(final int numJobs) {
		synchronized (jobListeners) {
			// each listener is notified on its own thread
			for (IJobListener jobListener : jobListeners) {
				jobNotifierPool.execute(new Runnable() {

					@Override
					public void run() {
						LOGGER.info("Notifying " + jobListener + " of "
								+ numJobs + " new jobs.");
						jobListener.newJobsAvailable(Publisher.this);
					}
				});
			}
		}
	}

	public boolean addJobListener(IJobListener jobListener) {
		if (jobListener == null)
			return false;
		synchronized (jobListeners) {
			return jobListeners.add(jobListener);
		}
	}

	public boolean removeJobListener(IJobListener jobListener) {
		synchronized (jobListeners) {
			return jobListeners.remove(jobListener);
		}
	}

	public void submitJobApplication(JobApplication jobApplication) {
		if (jobApplication == null || jobApplication.getRequestedJobCount() <= 0
				|| jobApplication.getManager() == null)
			return;

		int jobsRequested = jobApplication.getRequestedJobCount();
		Manager requestingManager = jobApplication.getManager();

		List<Job> jobsToAssign = new ArrayList<>(jobsRequested);
		Iterator<Job> i = availableJobs.iterator();
		while (i.hasNext() && jobsRequested > 0) {
			Job job = i.next();
			jobsToAssign.add(job);
			i.remove();
			jobsRequested--;
		}

		if (!jobsToAssign.isEmpty())
			requestingManager.assignJobs(jobsToAssign);
	}

	/**
	 * Used by {@link Manager}s to tell if a {@link Publisher} still has jobs
	 * that need to be done.
	 * 
	 * @return <code>true</code> if jobs still exist and <code>false</code>
	 *         otherwise.
	 */
	public boolean hasJobsAvailable() {
		// isEmpty() is much faster than size() for large Qs
		return !availableJobs.isEmpty();
	}

}
