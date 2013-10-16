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
	public static final int OVERFLOW_PROTOCOL_DROPOLDEST 	= 1;
	public static final int OVERFLOW_PROTOCOL_DROPNEWEST 	= 2;
	public static final int OVERFLOW_PROTOCOL_ERROR 		= 3;
	
	private int maxSize;
	private int actualSize;
	private int timeout;
	private int objectsSize[];
	private int overflowProtocol = OVERFLOW_PROTOCOL_DROPOLDEST;
	

	
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
	
	public void setOverflowProtocol (int p)
	{
		this.overflowProtocol = p;
	}
	
	public int getOverflowProtocol ()
	{
		return (this.overflowProtocol);
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
		for (int i = 1 ; i < this.maxSize ; i++)
		{
			objectsSize[i] = objectsSize[i+1];
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
		int position = 0;
		boolean useOverflow = false;
		
		if (! obj.isValid())
		{
			return;
		}
		System.out.println("actual size" + this.actualSize + " max size " + this.maxSize);

		if ((this.actualSize >= this.maxSize) && (this.overflowProtocol == OVERFLOW_PROTOCOL_ERROR))
		{
			Debug.debug("[InternalPortQueued] overflow protocol is error and queue is already full");
			return;
		}
		
		position = this.actualSize;
		
		if ((this.actualSize >= this.maxSize) && (this.overflowProtocol == OVERFLOW_PROTOCOL_DROPNEWEST))
		{
			Debug.debug("[InternalPortQueued] overflow protocol is dropoldest");
			System.out.println("dropoldest");
			position = this.maxSize;
			useOverflow = true;
		}
		
		if ((this.actualSize >= this.maxSize) && (this.overflowProtocol == OVERFLOW_PROTOCOL_DROPOLDEST))
		{
			Debug.debug("[InternalPortQueued] overflow protocol is dropoldest");
			position = 0;
			useOverflow = true;
		}
		
		
		Debug.debug("[InternalPortQueued] writeObject called");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(obj);
		  byte[] yourBytes = bos.toByteArray();
		  
		  for (int i = 0 ; i < position ; i++)
		  {
			  bufpos = bufpos + this.objectsSize[i];
		  }
		  
		  this.objectsSize[position] = yourBytes.length;
		  
		  if (! useOverflow)
		  {
			  this.actualSize = this.actualSize + 1;
		  }
		  
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
