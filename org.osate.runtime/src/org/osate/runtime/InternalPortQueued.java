package org.osate.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.osate.runtime.types.OjrType;

public class InternalPortQueued extends InternalPort
{
	private int maxSize;
	private int actualSize;
	private int timeout;
	private int objectsSize[];
	
	public InternalPortQueued ()
	{
		super ();
		this.actualSize = 0;
		this.maxSize = 1;
		this.timeout = 0;
		this.objectsSize = new int[this.maxSize];
		this.objectsSize [0] = 0;
	}
	
	public int getTimeout ()
	{
		return this.timeout;
	}
	
	public void setTimeout (int t)
	{
		this.timeout = t;
	}
	
	public void setSize (int newSize)
	{
		int tmp[];
		
		if (newSize > actualSize)
		{
			Debug.debug("[InternalPortQueued] setSize() returns because newSize is bigger than actualSize");

			return;
		}
		
		if (newSize == maxSize)
		{
			Debug.debug("[InternalPortQueued] setSize() returns because newSize is equal to maxSize");

			return;
		}
		
		tmp = new int[newSize];
		for (int i = 0 ; (i < actualSize) && (i < newSize) ; i++)
		{
			tmp[i] = objectsSize[i];
		}
		this.maxSize = newSize;
		this.objectsSize = tmp;
	}
	
	public int getSize ()
	{
		return this.maxSize;
	}

	public synchronized void readObject(OjrType obj)
	{
		int readSize = 0;
		while (this.actualSize == 0)
		{
			try 
			{
				wait(this.timeout);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				Debug.debug("[InternalPortQueued] InterruptedException exception in readObject()");
				e.printStackTrace();
			}
			obj.setValid(false);
			return;
		}
		
		Debug.debug("[InternalPortQueued] readObject called");
		readSize = objectsSize[0];
		byte[] tmp = new byte[readSize];
		for (int i = 0 ; i < readSize ; i++)
		{
			tmp[i] = buffer[i];
		}
		for (int i = 0 ; i < (this.bufsize - readSize) ; i++)
		{
			buffer[i] = buffer[readSize + i];
		}
		for (int i = 1 ; i < this.actualSize ; i++)
		{
			objectsSize[i] = buffer[readSize + i];
		}
		this.actualSize = this.actualSize - 1;
		ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
			OjrType newObj = (OjrType) in.readObject();
			newObj.copy(obj);
			obj.setValid(true);
		}
		catch (IOException ioe)
		{
			Debug.debug("[InternalPortQueued] io exception in InternalPort");
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		try {
		  bis.close();
		  in.close();
		}
		catch (IOException ioe)
		{
			Debug.debug("[InternalPortQueued] io exception in InternalPort");
			ioe.printStackTrace();
		}
		
		notifyAll();
		
		Debug.debug("[InternalPortQueued] readObject completed");
		
	}

	public synchronized void  writeObject(OjrType obj) 
	{
		int bufpos = 0;
		
		if (! obj.isValid())
		{
			return;
		}
		
		while (this.actualSize >= this.maxSize)
		{
			try 
			{
				
				wait(this.timeout);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				Debug.debug("[InternalPortQueued] InterruptedException exception in writeObject()");
				e.printStackTrace();
			}
		}
		Debug.debug("[InternalPortQueued] writeObject called");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(obj);
		  byte[] yourBytes = bos.toByteArray();
		  
		  for (int i = 0 ; i < this.actualSize ; i++)
		  {
			  bufpos = bufpos + this.objectsSize[i];
		  }
		  
		  this.objectsSize[this.actualSize] = yourBytes.length;
		  this.actualSize = this.actualSize + 1;
		  for (int i = 0 ; i < yourBytes.length ; i++)
		  {
			  buffer[bufpos + i] = yourBytes[i];
		  }
		} 
		catch (IOException ioe)
		{
			Debug.debug("[InternalPortQueued] io exception in InternalPort");
			ioe.printStackTrace();
		}

		try {
			out.close();
			bos.close();
		} catch (IOException e) {
			Debug.debug("[InternalPortQueued] io exception in InternalPort");
			e.printStackTrace();
		}
		  notifyAll();
			//System.out.println (" write completed");

		Debug.debug("[InternalPortQueued] writeObject completed");
	}
	
}
