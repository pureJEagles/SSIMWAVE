# SSIMWAVE Coding Challenge

The above files represent an Eclipse Java project, along with the source code, for the SSIMWAVE coding challenge.

Using Eclipse simply choose to clone an existing Git repository and use https://github.com/pureJEagles/SSIMWAVE.git as the repository URI.
Import the Java project as this will both create a Java project and compile the source.

If you are not using Eclipse, you will need to clone the repository using git and then compile the Java files under the 'src' folder.

Once compiled, navigate to the directory containing the compiled Java bytecode (i.e. 'bin' directory if you are using the Eclipse project).
From there, execute the program by entering the following command:

<pre>java com.ssimwave.challenge.CLIRunner</pre>

This will execute the program with the default parameters of 3 managers with 10 workers each.

You can change the number of managers and workers using program parameters as follows:

<pre>java com.ssimwave.challenge.CLIRunner [numManagers numWorkersPerManager]</pre>
If you specify a number of managers, you must also specify the number of workers (i.e. two parameters or nothing).
 
Command: <pre>java com.ssimwave.challenge.CLIRunner 5 40</pre>
Result: Runs program using 5 managers with 40 workers each.

Once running, the program will respond to two types of commands from the CLI:

1.  Type a number and hit <Enter>.<br>
    This will result in the creation of the specified number of jobs.
    
2.  Type 'quit' and hit <Enter>.
    This will result in the termination of the program and an output of the stats.
    
Commands can be entered while output is scrolling.

Enjoy!