package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class JavaProcess {

	private JavaProcess() {
	}

	public static int exec(Class klass, String...paramters) throws IOException,
			InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator
				+ "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
				className, "other paramerters");

		Process process = builder.start();
		
		Scanner scanner = new Scanner(process.getInputStream());
		while(scanner.hasNext())
			System.out.println(scanner.next());
		process.waitFor();
		return process.exitValue();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int status = JavaProcess.exec(test.class);
		System.out.println(status);
	}
}
