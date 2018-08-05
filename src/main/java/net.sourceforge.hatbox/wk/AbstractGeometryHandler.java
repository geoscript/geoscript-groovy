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
import java.io.EOFException;
import java.io.OutputStream;

import net.sourceforge.hatbox.wk.WKTParseState.Coord;

public abstract class AbstractGeometryHandler implements GeometryHandler, ParserConstants {

	private static final int BIG_ENDIAN = 0;

	public abstract void readWKB(InputStream is, final WKBParseState state) throws IOException;
	
	public abstract void writeWKB(OutputStream os, WKTParseState state) throws IOException, ParseException;

	public abstract WKTParseState handleToken(int token, final WKTParseState state) throws ParseException;
	
    public void readCoord(final InputStream is, final WKBParseState state) throws IOException {
        double x = getDouble(is, state);
        double y = getDouble(is, state);
        if (state.hasZ()) {
        	getBytes(is, state.getBytes(), 8);
        }
        state.addCoord(x, y);
    }
	
	public void readLinearRing(final InputStream is, final WKBParseState state) throws IOException {
		if (state.isBuildingString()) {
			state.openRing();
		}
        int coords = getInt(is, state);
        for (int i = 0; i < coords; i++) {
        	readCoord(is, state);
        }
		if (state.isBuildingString()) {
			state.closeRing();
		}
	}
    
    public static void getBytes(final InputStream is, final byte[] b, final int length) throws IOException {
    	int toRead = length;
    	int offset = 0;
    	int returned = is.read(b, offset, toRead);
    	while ((returned < toRead) && (returned >= 0)) {
    		returned = is.read(b, offset+=returned, toRead-=returned);
    	}
    	if (returned < 0) {
    		throw new EOFException();
    	}
    }
    
    public double getDouble(final InputStream is, final WKBParseState state) throws IOException {
    	return Double.longBitsToDouble(getLong(is, state));
    }
    
    public long getLong(final InputStream is, final WKBParseState state) throws IOException {
    	byte[] b = state.getBytes();
    	getBytes(is, b, 8);
    	if (state.getEndian() == BIG_ENDIAN) {
        	return ((((long)b[0] & 0xff) << 56) |
        			(((long)b[1] & 0xff) << 48) |
        			(((long)b[2] & 0xff) << 40) |
        			(((long)b[3] & 0xff) << 32) |
        			(((long)b[4] & 0xff) << 24) |
        			(((long)b[5] & 0xff) << 16) |
        			(((long)b[6] & 0xff) <<  8) |
        			(((long)b[7] & 0xff) <<  0));
    	} else {
        	return ((((long)b[7] & 0xff) << 56) |
        			(((long)b[6] & 0xff) << 48) |
        			(((long)b[5] & 0xff) << 40) |
        			(((long)b[4] & 0xff) << 32) |
        			(((long)b[3] & 0xff) << 24) |
        			(((long)b[2] & 0xff) << 16) |
        			(((long)b[1] & 0xff) <<  8) |
        			(((long)b[0] & 0xff) <<  0));
    	}
    }
    
    public static int getInt(final InputStream is, final WKBParseState state) throws IOException {
    	byte[] b = state.getBytes();
    	getBytes(is, b, 4);
    	if (state.getEndian() == BIG_ENDIAN) {
        	return (((b[0] & 0xff) << 24) |
        			((b[1] & 0xff) << 16) |
        			((b[2] & 0xff) <<  8) |
        			((b[3] & 0xff) <<  0));
    	} else {
        	return (((b[3] & 0xff) << 24) |
        			((b[2] & 0xff) << 16) |
        			((b[1] & 0xff) <<  8) |
        			((b[0] & 0xff) <<  0));
    	}
    }
    
    public static int getByte(final InputStream is) throws IOException {
    	int b = is.read();
    	if (b < 0) {
    		throw new EOFException();
    	} else {
        	return b;
    	}
    }
 	
	public void writeWKBHeader(OutputStream os, WKTParseState state) throws IOException, ParseException {
		if (state.getState() == TYPE_COMPL) {
			os.write(BIG_ENDIAN);
			writeInt(state.getType().getId(), os, state);
		} else {
			throw new ParseException("Type " + state.getType().name() + " not in complete state");
		}
		
	}
	
	public void writeCoord(OutputStream os, Coord coord, WKTParseState state) throws IOException {
		writeDouble(coord.x, os, state);
		writeDouble(coord.y, os, state);
	}
	
	public void writeInt(int val, OutputStream os, WKTParseState state) throws IOException {
		byte[] data = state.getIntBuf();
        data[0] = (byte)(val >> 24);
        data[1] = (byte)(val >> 16);
        data[2] = (byte)(val >> 8);
        data[3] = (byte)val;
        os.write(data);
	}
	
	public void writeDouble(double d, OutputStream os, WKTParseState state) throws IOException {
		byte[] data = state.getLongBuf();
		long val = Double.doubleToLongBits(d);
		data[0] = (byte)(val >> 56);
        data[1] = (byte)(val >> 48);
        data[2] = (byte)(val >> 40);
        data[3] = (byte)(val >> 32);
        data[4] = (byte)(val >> 24);
        data[5] = (byte)(val >> 16);
        data[6] = (byte)(val >> 8);
        data[7] = (byte)val;
        os.write(data);
	}

}