package org.osate.runtime;

public class Task {

	private int 	identifier;
	private Thread 	threadObject;
	private int		period;
	
	public Task (Thread t)
	{
		threadObject = t;
	}
	
	public void start ()
	{
		threadObject.start();
	}
	
	public void setPeriod (int p)
	{
		this.period = p;
	}
	
	public int getPeriod ()
	{
		return this.period;
	}
	
	public void setIdentifier (int i)
	{
		this.identifier = i;
	}
	
	public int getIdentifier ()
	{
		return this.identifier;
	}
	
	public Thread getThreadObject ()
	{
		return this.threadObject;
	}
	
	public void waitForNextPeriod ()
	{
		try
		{
			Thread.sleep(this.period);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
