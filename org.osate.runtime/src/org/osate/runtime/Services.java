package org.osate.runtime;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.osate.runtime.types.OjrType;
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

	public static void SendData(InternalPort port, OjrType obj, Status status)
	{
		if (port == null)
		{
			Debug.debug ("SendData - port is null");
			return;
		}
		
		if (obj == null)
		{
			Debug.debug ("SendData - obj is null");
			return;
		}
		
		if (status == null)
		{
			Debug.debug ("SendData - status is null");
			return;
		}
		port.writeObject(obj);
	}
	
	public static void ReceiveData(InternalPort port, OjrType obj, Status status)
	{
		if (port == null)
		{
			Debug.debug ("SendData - port is null");
			return;
		}
		
		if (obj == null)
		{
			Debug.debug ("SendData - obj is null");
			return;
		}
		
		if (status == null)
		{
			Debug.debug ("SendData - status is null");
			return;
		}
		
		port.readObject(obj);
	}
}
