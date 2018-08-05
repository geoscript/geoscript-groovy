/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2011 Peter Yuill
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sourceforge.hatbox.wk;

import java.io.InputStream;
import java.io.IOException;

    
/**
 * A much more efficient version of ByteArrayInputStream
 * 
 * @author Peter Yuill
 */
public class WKBArrayInputStream extends InputStream {
	
	private byte[] wkb;
	private int offset;
	private int length;
	
	public WKBArrayInputStream(byte[] wkb) {
		this.wkb = wkb; 
		offset = 0;
		length = wkb.length;
	}
	
	@Override
	public int read() throws IOException {
		if (offset < length) {
			return wkb[offset++] & 0xff;
		} else {
			return -1;
		}
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int size = b.length;
		if ((offset + size) <= length) {
			System.arraycopy(wkb, offset, b, 0, size);
			offset+=size;
			return size;
		} else {
			// All or nothing, do not partially fill a buffer
			throw new IOException("Attempted to read beyond data array");
		}
	}
}
