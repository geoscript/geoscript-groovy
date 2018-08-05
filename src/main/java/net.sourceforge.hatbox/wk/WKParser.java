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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;

/**
 * WKB utilities
 * 
 * @author Peter Yuill
 */
public class WKParser implements ParserConstants {
	
    /**
     * Usage is static
     */
    private WKParser() {
    }
	
    /**
     * Create an envelope from its WKB representation as byte array
     * 
     * @param wkb The Well Known Binary input
     * @return The constructed envelope
     */
	public static WKBEnvelope readEnvelope(byte[] wkb) throws IOException {
        if (wkb == null) {
        	throw new IllegalArgumentException("WKB byte array not provided");
        }
		WKBParseState state = new WKBParseState();
		state.setEnvelope(new WKBEnvelope());
        parseWKB(new WKBArrayInputStream(wkb), state);
        return state.getEnvelope();
	}
	
    /**
     * Create an envelope from its WKB representation as InputStream
     * 
     * @param wkb The Well Known Binary input
     * @return The constructed envelope
     */
	public static WKBEnvelope readEnvelope(InputStream wkb) throws IOException {
        if (wkb == null) {
        	throw new IllegalArgumentException("WKB InputStream not provided");
        }
		WKBParseState state = new WKBParseState();
		state.setEnvelope(new WKBEnvelope());
		parseWKB(wkb, state);
        return state.getEnvelope();
	}
    
	
    /**
     * Create WKT from its WKB representation as byte array
     * 
     * @param wkb The Well Known Binary input
     * @return The Well Known Text output
     */
	public static String toWKT(byte[] wkb) throws IOException {
        if (wkb == null) {
        	throw new IllegalArgumentException("WKB byte array not provided");
        }
		WKBParseState state = new WKBParseState();
		state.setStringBuilder(new StringBuilder());
		parseWKB(new WKBArrayInputStream(wkb), state);
        return state.getStringBuilder().toString();
	}
	
    /**
     * Create WKT from its WKB representation as InputStream
     * 
     * @param wkb The Well Known Binary input
     * @return The Well Known Text output
     */
	public static String toWKT(InputStream wkb) throws IOException {
        if (wkb == null) {
        	throw new IllegalArgumentException("WKB InputStream not provided");
        }
		WKBParseState state = new WKBParseState();
		state.setStringBuilder(new StringBuilder());
		parseWKB(wkb, state);
        return state.getStringBuilder().toString();
	}
	
    /**
     * Create WKB from its WKT representation as a String
     * 
     * @param wkb The Well Known Text input
     * @return The Well Known Binay output
     */
	public static byte[] parseWKT(String wkt) throws IOException, ParseException {
        if (wkt == null) {
        	throw new IllegalArgumentException("WKT String not provided");
        }
		return parseWKT(new StringReader(wkt));
	}
	
    /**
     * Create WKB from its WKT representation as a Reader
     * 
     * @param wkb The Well Known Text input
     * @return The Well Known Binay output
     */
	public static byte[] parseWKT(final Reader wkt) throws IOException, ParseException {
        if (wkt == null) {
        	throw new IllegalArgumentException("WKT Reader not provided");
        }
		StreamTokenizer t = new StreamTokenizer(wkt);
		StringBuilder buf = new StringBuilder();
		WKTParseState state = null;
		int token = t.nextToken();
		while (token != StreamTokenizer.TT_EOF) {
			switch(token) {
			case StreamTokenizer.TT_WORD :
				String typeName = t.sval.toUpperCase();
				buf.append(typeName);
				WKBGeometryType type = null;
				try {
					type = WKBGeometryType.getByName(typeName);
				} catch (IllegalArgumentException iae) {
					throw new ParseException("Unregognized type name : " + buf + " <===");
				}
				if (state == null) {
					state = new WKTParseState();
					state.setType(type);
					state.setBuf(buf);
				} else if (state.getState() == NEW_GEOMETRY) {
					state.setState(GEOMETRY_COMPL);
					WKTParseState child = new WKTParseState();
					child.setType(type);
					child.setBuf(buf);
					state.addChild(child);
					state = child;
				} else {
					throw new ParseException("Type name found but not expected: " + buf + " <===");
				}
				break;
			case StreamTokenizer.TT_NUMBER :
				buf.append(' ');
				buf.append(t.nval);
				if (state == null) {
					throw new ParseException("No type set: " + buf);
				}
				state = state.setOrd(t.nval);
				break;
			case '(' :
			case ')' :
			case ',' :
				buf.append((char)token);
				if (state == null) {
					throw new ParseException("No type set: " + buf);
				} else {
//					System.out.print((char)token + " " + "In " + state.getType() + " " + state.getState());
					state = state.getType().getHandler().handleToken(token, state);
//					System.out.println(" Out " + state.getType() + " " + state.getState());
				}
				break;
			default :
				throw new ParseException("Unexpected token : " + buf + " <===");
			}
			token = t.nextToken();
		}
		if (state == null) {
			throw new ParseException("No type set: " + buf);
		}
		if (state.getParent() != null) {
			throw new ParseException("Not properly closed: " + buf);
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		state.getType().getHandler().writeWKB(os, state);
		os.close();
		return os.toByteArray();
	}
	
	public static WKBGeometryType parseWKB(final InputStream is, final WKBParseState state) throws IOException {
	    state.setEndian(AbstractGeometryHandler.getByte(is));
		int typeEtc = AbstractGeometryHandler.getInt(is, state);
		WKBGeometryType type = WKBGeometryType.getById(typeEtc & 0xff);
		state.setType(type);
		//PostGIS enhancements
		state.setHasZ((typeEtc & 0x80000000) != 0);
		boolean srid = (typeEtc & 0x20000000) != 0;
		if (srid) {
			AbstractGeometryHandler.getInt(is, state);
		}
		
		if (state.isBuildingString()) {
			if (state.getParent() == null) {
		    	state.openType(type.name());
			} else if (state.getParent().getType().isCollection()) {
		    	state.openType(type.name());
			} else {
		    	state.openType(null);
			}
			type.getHandler().readWKB(is, state);
	    	state.closeType();
		} else {
			type.getHandler().readWKB(is, state);
		}
		return type;
	}
	
}
