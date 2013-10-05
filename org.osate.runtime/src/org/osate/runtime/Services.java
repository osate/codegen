package org.osate.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.osate.runtime.types.Status;

public class Services
{
	private static List<Task> tasks = new ArrayList<Task>(); 

	public static void createTask (Thread t, int taskIdentifier, int taskPeriod)
	{
		System.out.println("[Services] Create task id " + taskIdentifier);
		Task task = new Task(t);
		task.setIdentifier(taskIdentifier);
		task.setPeriod (taskPeriod); 
		tasks.add(task);
		task.start();
	}
	
	public static void WaitNextPeriod (Status status)
	{
		System.out.println("[Services] WaitNextPeriod");

		for (Task t : tasks)
		{
			if (t.getThreadObject() == Thread.currentThread())
			{
				int period = t.getPeriod();
				try {
					Thread.sleep(period);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void waitEnd ()
	{
		for (Task t : tasks)
		{
			try {
				t.getThreadObject().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
