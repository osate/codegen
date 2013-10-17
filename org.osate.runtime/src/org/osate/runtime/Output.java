package org.osate.runtime;

public class Output {
	public final static boolean USE_OUTPUT = true;
	
	public static void output (String s)
	{
		if (USE_OUTPUT)
		{
			System.out.println (s);
		}
	}
	
	
	public static void outputWithTime (String s)
	{
		long ms = System.currentTimeMillis();
		if (USE_OUTPUT)
		{
			System.out.println ("[" + (ms - Services.startTime) +"] - " + s);
		}
	}
}
