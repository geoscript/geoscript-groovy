/*
 *    HatBox : A user-space spatial add-on for the Java databases
 *    
 *    Copyright (C) 2007 - 2009 Peter Yuill
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
package net.sourceforge.hatbox;

import java.sql.SQLException;

/**
 * 
 * @author Peter Yuill
 */
public interface RTreeSession {
    
    public long getRootId() throws SQLException;
    
    public void setRootId(long id) throws SQLException;
    
    public Node getNode(long id) throws SQLException;
    
    public long insertNode(Node node) throws SQLException;
    
    public void updateNode(Node node) throws SQLException;
    
    public void deleteNode(Node node) throws SQLException;
}
