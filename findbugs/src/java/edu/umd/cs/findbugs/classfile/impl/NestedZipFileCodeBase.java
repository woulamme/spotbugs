/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006, University of Maryland
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.classfile.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.IScannableCodeBase;
import edu.umd.cs.findbugs.classfile.ResourceNotFoundException;
import edu.umd.cs.findbugs.io.IO;

/**
 * A scannable code base class for a zip (or Jar) file nested inside
 * some other codebase.  These are handled by extracting the nested
 * zip/jar file to a temporary file, and delegating to an
 * internal ZipFileCodeBase that reads from the temporary file.
 * 
 * @author David Hovemeyer
 */
public class NestedZipFileCodeBase extends AbstractScannableCodeBase implements IScannableCodeBase {
	private ICodeBase parentCodeBase;
	private String resourceName;
	private File tempFile;
	private ZipFileCodeBase delegate;

	/**
	 * Constructor.
	 * 
	 * @param parentCodeBase the parent code base (in which the zip or jar file is located)
	 * @param resourceName   name of resource containing the nested zip or jar file
	 */
	public NestedZipFileCodeBase(ICodeBase parentCodeBase, String resourceName)
			throws ResourceNotFoundException, IOException {
		this.parentCodeBase = parentCodeBase;
		this.resourceName = resourceName;
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			// Create a temp file
			this.tempFile = File.createTempFile("findbugs", ".zip");
			tempFile.deleteOnExit(); // just in case we crash before the codebase is closed
			
			// Copy nested zipfile to the temporary file
			inputStream = parentCodeBase.openResource(resourceName);
			outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
			IO.copy(inputStream, outputStream);
			outputStream.flush();
			
			// Create the delegate to read from the temporary file
			delegate = new ZipFileCodeBase(tempFile);
		} finally {
			if (inputStream != null) {
				IO.close(inputStream);
			}
			
			if (outputStream != null) {
				IO.close(outputStream);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see edu.umd.cs.findbugs.classfile.IScannableCodeBase#resourceNameIterator()
	 */
	public Iterator<String> resourceNameIterator() {
		return delegate.resourceNameIterator();
	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.findbugs.classfile.ICodeBase#openResource(java.lang.String)
	 */
	public InputStream openResource(String resourceName)
			throws ResourceNotFoundException, IOException {
		return delegate.openResource(resourceName);
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.findbugs.classfile.ICodeBase#close()
	 */
	public void close() {
		delegate.close();
		tempFile.delete();
	}

}
