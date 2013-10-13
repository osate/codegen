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

public class InternalPortSampled extends InternalPort
{


	public void writeObject (OjrType obj)
	{
		Debug.debug("[InternalPortSampled] writeObject called");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(obj);
		  byte[] yourBytes = bos.toByteArray();
		  this.bufsize = yourBytes.length; 
		  for (int i = 0 ; i < yourBytes.length ; i++)
		  {
			  buffer[i] = yourBytes[i];
		  }
		} 
		catch (IOException ioe)
		{
			Debug.debug("[InternalPortSampled] io exception in InternalPort");
			ioe.printStackTrace();
		}

		try {
			out.close();
			bos.close();
		} catch (IOException e) {
			Debug.debug("[InternalPortSampled] io exception in InternalPort");
			e.printStackTrace();
		}
		  
		Debug.debug("[InternalPortSampled] writeObject completed");
	}
	
	public void readObject (OjrType obj)
	{
		
		if (this.bufsize == 0)
		{
			Debug.debug("[InternalPortSampled] readObject - size == 0");
			return;
		}
		
		Debug.debug("[InternalPortSampled] readObject called");

		byte[] tmp = new byte[this.bufsize];
		for (int i = 0 ; i < this.bufsize ; i++)
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
			Debug.debug("[InternalPortSampled] io exception in InternalPort");
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
			Debug.debug("[InternalPortSampled] io exception in InternalPort");
			ioe.printStackTrace();
		}
		
		Debug.debug("[InternalPortSampled] readObject completed");
	}
}
