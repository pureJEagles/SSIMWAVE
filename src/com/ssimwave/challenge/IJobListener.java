package com.ssimwave.challenge;

/**
 * The interface by which a {@link Publisher} notifies interested parties of
 * newly created/available jobs.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public interface IJobListener {

	/**
	 * Callback for newly created jobs.
	 * 
	 * @param publisher
	 *            the {@link Publisher} that created the jobs.
	 */
	void newJobsAvailable(Publisher publisher);
}
