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

public interface ParserConstants {
	
	public static int BIG_ENDIAN = 0;
	
	public static int OPEN_TOKEN = '(';
	public static int CLOSE_TOKEN = ')';
	public static int NEXT_TOKEN = ',';
	
	public static int NEW_TYPE = 0;
	public static int NEW_CHILD = 1;
	public static int NEW_GEOMETRY = 2;
	public static int NEW_RING = 3;
	public static int X = 4;
	public static int Y = 5;
	public static int COORD_COMPL = 20;
	public static int RING_COMPL = 21;
	public static int TYPE_COMPL = 22;
	public static int GEOMETRY_COMPL = 25;
	
}
