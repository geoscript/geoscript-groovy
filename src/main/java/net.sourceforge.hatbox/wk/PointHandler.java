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
import java.io.OutputStream;

public class PointHandler extends AbstractGeometryHandler {
	@Override
	public void readWKB(InputStream is, final WKBParseState work) throws IOException {
    	readCoord(is, work);
	}

	@Override
	public void writeWKB(OutputStream os, WKTParseState state) throws IOException, ParseException {
		writeWKBHeader(os, state);
		writeCoord(os, state.getCoord(), state);
	}

	@Override
	public WKTParseState handleToken(int token, WKTParseState state) throws ParseException {
		return state.pointToken(token);
	}
}