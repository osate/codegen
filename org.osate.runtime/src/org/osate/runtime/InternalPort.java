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

public abstract class InternalPort
{
	protected byte buffer[];
	protected int bufsize;
	public static final int BUFFER_SIZE = 128*1024;
	public InternalPort ()
	{
		this.buffer = new byte[BUFFER_SIZE];
	}
	
	public abstract void readObject (OjrType obj);
	public abstract void writeObject (OjrType obj);

}
