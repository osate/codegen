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

public class InternalPort
{
	public byte buffer[];
	public int size;
	
	public InternalPort ()
	{
		this.size = 0;
		this.buffer = new byte[1024];
	}
	
	public void writeObject (OjrType obj)
	{
		Debug.debug("writeObject called");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(obj);
		  byte[] yourBytes = bos.toByteArray();
		  this.size = yourBytes.length;
		  for (int i = 0 ; i < yourBytes.length ; i++)
		  {
			  buffer[i] = yourBytes[i];
		  }
		} 
		catch (IOException ioe)
		{
			Debug.debug("io exception in InternalPort");
			ioe.printStackTrace();
		}

		try {
			out.close();
			bos.close();
		} catch (IOException e) {
			Debug.debug("io exception in InternalPort");
			e.printStackTrace();
		}
		  
		Debug.debug("writeObject completed");
	}
	
	public void readObject (OjrType obj)
	{
		
		if (this.size == 0)
		{
			Debug.debug("readObject - size == 0");
			return;
		}
		
		Debug.debug("readObject called");

		byte[] tmp = new byte[this.size];
		for (int i = 0 ; i < this.size ; i++)
		{
			tmp[i] = buffer[i];
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
			OjrType newObj = (OjrType) in.readObject();
			newObj.copy(obj);
		}
		catch (IOException ioe)
		{
			Debug.debug("io exception in InternalPort");
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
			Debug.debug("io exception in InternalPort");
			ioe.printStackTrace();
		}
		
		Debug.debug("readObject completed");
	}
}
