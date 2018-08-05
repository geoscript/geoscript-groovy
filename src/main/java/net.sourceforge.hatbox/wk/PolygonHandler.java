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
import java.util.List;

import net.sourceforge.hatbox.wk.WKTParseState.Coord;

public class PolygonHandler extends AbstractGeometryHandler {
	@Override
	public void readWKB(InputStream is, final WKBParseState work) throws IOException {
    	int rings = getInt(is, work);
        for(int i = 0; i < rings; i++) {
        	readLinearRing(is, work);
        }
	}

	@Override
	public void writeWKB(OutputStream os, WKTParseState state) throws IOException, ParseException {
		writeWKBHeader(os, state);
		writeInt(state.getLinestrings().size(), os, state);
		for (List<Coord> ring : state.getLinestrings()) {
			writeInt(ring.size(), os, state);
			for (Coord c : ring) {
				writeCoord(os, c, state);
			}
		}
	}

	@Override
	public WKTParseState handleToken(int token, WKTParseState state) throws ParseException {
		return state.polygonToken(token);
	}
}