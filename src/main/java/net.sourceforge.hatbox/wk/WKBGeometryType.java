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

public enum WKBGeometryType {
	POINT(null, new PointHandler(), false, false),
	LINESTRING(null, new LinestringHandler(), false, false),
	POLYGON(null, new PolygonHandler(), false, false),
	MULTIPOINT(POINT, new MultiHandler(), false, false),
	MULTILINESTRING(LINESTRING, new MultiHandler(), false, false),
	MULTIPOLYGON(POLYGON, new MultiHandler(), false, false),
	GEOMETRYCOLLECTION(null, new CollectionHandler(), true, false),
	BROKEN_MULTIPOINT(null, new LinestringHandler(), false, true);
	
	private WKBGeometryType componentType;
	private GeometryHandler handler;
	private boolean collection;
	private boolean internal;
	
	WKBGeometryType(WKBGeometryType componentType, GeometryHandler handler, boolean collection, boolean internal) {
		this.componentType = componentType;
		this.handler = handler;
		this.collection = collection;
		this.internal = internal;
	}
	
	public int getId() {
		return ordinal() + 1;
	}
	
	public WKBGeometryType getComponentType() {
		return componentType;
	}

	public boolean isCollection() {
		return collection;
	}
	
	public boolean isInternal() {
		return internal;
	}

	public GeometryHandler getHandler() {
		return handler;
	}

	public static WKBGeometryType getById(int id) {
		if ((id > 0) && (id < WKBGeometryType.values().length)) { // exclude BROKEN_MULTIPOINT
			return WKBGeometryType.values()[id - 1];
		} else {
			throw new IllegalArgumentException("Not a legal WKBGeometryType id: " + id);
		}
	}
	
	public static WKBGeometryType getByName(String name) {
		WKBGeometryType type = WKBGeometryType.valueOf(name);
		if (type.isInternal()) {
			throw new IllegalArgumentException("Not a legal WKBGeometryType: " + name);
		} else {
			return type;
		}
	}

}