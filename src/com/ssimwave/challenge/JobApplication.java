package com.ssimwave.challenge;

/**
 * Represents an application submitted by {@link Manager} to a {@link Publisher}
 * for one of more jobs. Instances of {@link JobApplication} are immutable.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public class JobApplication {

	private final Manager manager;

	private final int requestedJobCount;

	/**
	 * Constructor.
	 * 
	 * @param manager
	 *            the {@link Manager} requesting the jobs.
	 * @param requestedJobCount
	 *            the number of jobs being requested.
	 */
	public JobApplication(Manager manager, int requestedJobCount) {
		this.manager = manager;
		this.requestedJobCount = requestedJobCount;
	}

	public Manager getManager() {
		return manager;
	}

	public int getRequestedJobCount() {
		return requestedJobCount;
	}

}
