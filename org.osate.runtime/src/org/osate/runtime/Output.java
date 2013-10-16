package org.osate.runtime;

public class Output {
	public final static boolean USE_OUTPUT = false;
	
	public static void output (String s)
	{
		if (USE_OUTPUT)
		{
			System.out.println (s);
		}
	}
}
