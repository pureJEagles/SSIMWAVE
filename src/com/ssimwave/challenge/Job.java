package com.ssimwave.challenge;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Represents a unit of work that can be done (i.e. run) by a worker. Instances
 * of {@link Job} are immutable.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public class Job implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(Job.class.getName());

	private static final AtomicInteger JOB_ID = new AtomicInteger(1);

	private final int durationInSecs;

	private final int jobID;

	public Job() {
		this.jobID = JOB_ID.getAndIncrement();
		Random random = new Random();
		this.durationInSecs = random.nextInt(5) + 1;
	}

	@Override
	public void run() {
		try {
			// causes the executing worker thread to pause, simulating work
			Thread.sleep(durationInSecs * 1000);
		} catch (InterruptedException e) {
			LOGGER.warning(e.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + durationInSecs;
		result = prime * result + jobID;
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
		Job other = (Job) obj;
		if (durationInSecs != other.durationInSecs)
			return false;
		if (jobID != other.jobID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Job-").append(jobID).append(" of duration ")
				.append(durationInSecs).append(" seconds");
		return sb.toString();
	}

}
