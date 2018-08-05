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

import java.util.List;
import java.util.ArrayList;

public class WKTParseState implements ParserConstants {
		
	private WKBGeometryType type;
	private int state = NEW_TYPE;
	
	private Coord coord;
	private List<Coord> linestring;
	private List<List<Coord>> linestrings;
	
	private WKTParseState parent;
	private List<WKTParseState> children;
	
	private StringBuilder buf;
	private byte[] intBuf = new byte[4];
	private byte[] longBuf = new byte[8];
	
	public WKTParseState pointToken(int token) throws ParseException {
		if (token == OPEN_TOKEN) {
			if (state == NEW_TYPE) {
				coord = new Coord();
				state = X;
				return this;
			} else {
				throw new ParseException("Misplaced open: " + buf + " <===");
			}
		} else if (token == CLOSE_TOKEN) {
			if (state == COORD_COMPL) {
				state = TYPE_COMPL;
				if (parent == null) {
					return this;
				} else {
					return parent;
				}
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else {
			throw new ParseException("Misplaced token: " + buf + " <===");
		}
	}
	
	public WKTParseState linestringToken(int token) throws ParseException {
		if (token == OPEN_TOKEN) { 
			if (state == NEW_TYPE) {
				coord = new Coord();
				state = X;
				return this;
			} else {
				throw new ParseException("Misplaced open: " + buf + " <===");
			}
		} else if (token == CLOSE_TOKEN) {
			if (state == COORD_COMPL) {
				if (type.equals(WKBGeometryType.BROKEN_MULTIPOINT)) {
					return convertBrokenToMultiPoint();
				}
				if (linestring == null) {
					throw new ParseException("Linestring must have at least 2 coordinates: " + buf + " <===");
				}
				state = TYPE_COMPL;
				linestring.add(coord);
				coord = null;
				if (parent == null) {
					return this;
				} else {
					return parent;
				}
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else if (token == NEXT_TOKEN) {
			if (state == COORD_COMPL) {
				if (linestring == null) {
					linestring = new ArrayList<Coord>();
				}
				linestring.add(coord);
				coord = new Coord();
				state = X;
				return this;
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else {
			throw new ParseException("Misplaced token: " + buf + " <===");
		}
	}
	
	public WKTParseState polygonToken(int token) throws ParseException {
		if (token == OPEN_TOKEN) {
			if (state == NEW_TYPE) {
				state = NEW_RING;
				return this;
			} else if (state == NEW_RING) {
				coord = new Coord();
				state = X;
				return this;
			} else {
				throw new ParseException("Misplaced open: " + buf + " <===");
			}
		} else if (token == CLOSE_TOKEN) {
			if (state == COORD_COMPL) {
				if (linestring == null) {
					linestring = new ArrayList<Coord>();
				}
				linestring.add(coord);
				coord = null;
				state = RING_COMPL;
				return this;
			} else if (state == RING_COMPL) {
				if (linestrings == null) {
					linestrings = new ArrayList<List<Coord>>();
				}
				linestrings.add(linestring);
				linestring = null;
				state = TYPE_COMPL;
				if (parent == null) {
					return this;
				} else {
					return parent;
				}
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else if (token == NEXT_TOKEN) {
			if (state == COORD_COMPL) {
				if (linestring == null) {
					linestring = new ArrayList<Coord>();
				}
				linestring.add(coord);
				coord = new Coord();
				state = X;
				return this;
			} else if (state == RING_COMPL) {
				if (linestrings == null) {
					linestrings = new ArrayList<List<Coord>>();
				}
				linestrings.add(linestring);
				linestring = null;
				state = NEW_RING;
				return this;
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else {
			throw new ParseException("Misplaced token: " + buf + " <===");
		}
	}
	
	public WKTParseState multiToken(int token) throws ParseException {
		if (token == OPEN_TOKEN) {
			if (state == NEW_TYPE) {
				state = NEW_CHILD;
				WKTParseState child = new WKTParseState();
				child.setType(type.getComponentType());
				child.setBuf(buf);
				addChild(child);
				return child;
			} else {
				throw new ParseException("Misplaced open: " + buf + " <===");
			}
		} else if (token == CLOSE_TOKEN) {
			if (state == NEW_CHILD) {
				state = TYPE_COMPL;
				if (parent == null) {
					return this;
				} else {
					return parent;
				}
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else if (token == NEXT_TOKEN) {
			if (state == NEW_CHILD) {
				WKTParseState child = new WKTParseState();
				child.setType(type.getComponentType());
				child.setBuf(buf);
				addChild(child);
				return child;
			} else {
				throw new ParseException("Misplaced ,: " + buf + " <===");
			}
		} else {
			throw new ParseException("Misplaced token: " + buf + " <===");
		}
	}
	
	public WKTParseState collectionToken(int token) throws ParseException {
		if (token == OPEN_TOKEN) {
			if (state == NEW_TYPE) {
				state = NEW_GEOMETRY;
				return this;
			} else {
				throw new ParseException("Misplaced open: " + buf + " <===");
			}
		} else if (token == CLOSE_TOKEN) {
			if (state == GEOMETRY_COMPL) {
				state = TYPE_COMPL;
				if (parent == null) {
					return this;
				} else {
					return parent;
				}
			} else {
				throw new ParseException("Misplaced close: " + buf + " <===");
			}
		} else if (token == NEXT_TOKEN) {
			if (state == GEOMETRY_COMPL) {
				state = NEW_GEOMETRY;
				return this;
			} else {
				throw new ParseException("Misplaced ,: " + buf + " <===");
			}
		} else {
			throw new ParseException("Misplaced token: " + buf + " <===");
		}
	}
	
	public WKTParseState setOrd(double ord) throws ParseException {
		if (state == X) {
			coord.x = ord;
			state = Y;
			return this;
		} else if (state == Y) {
			coord.y = ord;
			state = COORD_COMPL;
			return this;
		} else if ((state == NEW_TYPE) && (parent != null) && (parent.type.equals(WKBGeometryType.MULTIPOINT)) && (parent.children.size() == 1)) {
			// ie this the *first* ord in a broken multi-point
			parent.type = WKBGeometryType.BROKEN_MULTIPOINT;
			parent.children.clear();
			parent.coord = new Coord();
			parent.coord.x = ord;
			parent.state = Y;
			return parent;
		} else {
			throw new ParseException("Number not expected here: " + buf + " <===");
		}
	}
	
	private WKTParseState convertBrokenToMultiPoint() {
		type = WKBGeometryType.MULTIPOINT;
		if (linestring == null) {
			linestring = new ArrayList<Coord>();
		}
		state = TYPE_COMPL;
		linestring.add(coord);
		coord = null;
		for (Coord coord : linestring) {
			WKTParseState child = new WKTParseState();
			child.setType(type.getComponentType());
			child.setCoord(coord);
			child.setState(TYPE_COMPL);
			child.setBuf(buf);
			addChild(child);
		}
		linestring = null;
		if (parent == null) {
			return this;
		} else {
			return parent;
		}
	}
	
	public WKBGeometryType getType() {
		return type;
	}
	
	public void setType(WKBGeometryType type) {
		this.type = type;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public Coord getCoord() {
		return coord;
	}
	
	public void setCoord(Coord coord) {
		this.coord = coord;
	}
	
	public List<Coord> getLinestring() {
		return linestring;
	}
	
	public void setLinestring(List<Coord> linestring) {
		this.linestring = linestring;
	}
	
	public List<List<Coord>> getLinestrings() {
		return linestrings;
	}
	
	public void setLinestrings(List<List<Coord>> linestrings) {
		this.linestrings = linestrings;
	}
	
	public WKTParseState getParent() {
		return parent;
	}

	public void setParent(WKTParseState parent) {
		this.parent = parent;
	}
	
	public void setBuf(StringBuilder buf) {
		this.buf = buf;
	}

	public byte[] getIntBuf() {
		return intBuf;
	}

	public byte[] getLongBuf() {
		return longBuf;
	}

	public List<WKTParseState> getChildren() {
		return children;
	}

	public void addChild(WKTParseState child) {
		child.setParent(this);
		if (children == null) {
			children = new ArrayList<WKTParseState>();
		}
		children.add(child);
	}

	public static class Coord {
		public double x;
		public double y;
	}
	
}
