package org.osate.runtime.types;

public interface OjrType {
	public void copy (Object dst);
	public boolean isValid();
	public void setValid (boolean b);
}
