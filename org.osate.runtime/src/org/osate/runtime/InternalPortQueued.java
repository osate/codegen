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
	private int size;
	
	public void setSize (int s)
	{
		this.size = s;
	}
	
}
