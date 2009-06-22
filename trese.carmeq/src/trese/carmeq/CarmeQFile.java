/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.carmeq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class CarmeQFile
{
	/**
	 * The source file
	 */
	protected IFile file;

	/**
	 * Timestamp of the last loaded state
	 */
	protected long timestamp;

	/**
	 * True if it contains unsaved properties
	 */
	protected boolean dirty;

	/**
	 * The xadl file to work on
	 */
	protected IFile xadlFile;

	/**
	 * List of prolog files to include (in order)
	 */
	protected List<IFile> prologFiles = new ArrayList<IFile>();

	/**
	 * The prolog query
	 */
	protected String query;

	public CarmeQFile(IFile carmeqFile)
	{
		file = carmeqFile;
	}

	/**
	 * Load the contents of the file
	 */
	public void load() throws CoreException
	{}

	/**
	 * Save the contents to the file
	 */
	public void save() throws CoreException
	{}

	/**
	 * @return True if it contains unsaved data
	 */
	public boolean isDirty()
	{
		return dirty;
	}

	/**
	 * @return The timestamp this resource was last modified
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return True if this object and the file have equal timestamps
	 */
	public boolean isInSync()
	{
		IPath path = file.getLocation();
		if (path == null)
		{
			return false;
		}
		return isInSync(path.toFile());
	}

	/**
	 * @param localFile
	 * @return
	 */
	public boolean isInSync(File localFile)
	{
		return localFile.exists() && localFile.lastModified() == getTimestamp();
	}

	/**
	 * @param value
	 */
	public void setXADLFile(IFile value)
	{
		if (xadlFile != value)
		{
			xadlFile = value;
			dirty = true;
		}
	}

	/**
	 * @return
	 */
	public List<IFile> getPrologFiles()
	{
		return prologFiles;
	}

	public boolean addPrologFile(IFile newFile)
	{
		if (prologFiles.contains(newFile))
		{
			return false;
		}
		if (prologFiles.add(newFile))
		{
			dirty = true;
			return true;
		}
		return false;
	}

	/**
	 * @param o
	 * @return
	 */
	public boolean removePrologFile(IFile value)
	{
		if (prologFiles.remove(value))
		{
			dirty = true;
			return true;
		}
		return false;
	}

}
