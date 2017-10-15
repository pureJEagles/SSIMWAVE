package com.ssimwave.challenge;

/**
 * The program entry point responsible for reading command line parameters for
 * initial execution.
 * 
 * @author Jon Eagles (jon_eagles@yahoo.com)
 *
 */
public class CLIRunner {

	private static final int DEFAULT_MANAGERS = 3;

	private static final int DEFAULT_WORKERS_PER_MANAGER = 10;

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();

		int numManagers = DEFAULT_MANAGERS;
		int numWorkersPerManager = DEFAULT_WORKERS_PER_MANAGER;
		if (args.length == 2) {
			try {
				numManagers = Integer.parseInt(args[0]);
				numWorkersPerManager = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Bad parameters. Resorting to defaults.");
				numManagers = DEFAULT_MANAGERS;
				numWorkersPerManager = DEFAULT_WORKERS_PER_MANAGER;
			}
		} else if (args.length > 0)
			sb.append("Bad parameters. Resorting to defaults.\n");

		sb.append("Usage: java com.ssimwave.challenge.CLIRunner")
				.append(" [numManagers numWorkersPerManager]")
				.append("\nCreating ").append(numManagers)
				.append(" managers with ").append(numWorkersPerManager)
				.append(" workers each.");
		System.out.println(sb.toString());

		Publisher publisher = new Publisher();
		for (int i = 0; i < numManagers; i++) {
			Manager mgr = new Manager(numWorkersPerManager);
			publisher.addJobListener(mgr);
		}
		publisher.start();
	}

}
