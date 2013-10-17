package org.osate.runtime;

public class Debug 
{
	private final static boolean USE_DEBUG = false;
	
	public static void debug (String str)
	{
		if (USE_DEBUG)
		{
			System.err.println ("[OJR] " + str);
		}
	}
	
	
}
