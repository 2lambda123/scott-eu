// Start of user code Copyright
/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *     Michael Fiedler      - Bugzilla adpater implementations
 *     Jad El-khoury        - initial implementation of code generator (https://bugs.eclipse.org/bugs/show_bug.cgi?id=422448)
 * 
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package eu.scott.warehouse.domains.blocksworld;

import org.eclipse.lyo.oslc4j.core.model.OslcConstants;


// Start of user code imports
// End of user code

public interface BworldDomainConstants
{
    // Start of user code user constants
    // End of user code

    public static String BLOCKSWORLD_DOMAIN_DOMAIN = "http://ontology.cf.ericsson.net/pddl_example/";
    public static String BLOCKSWORLD_DOMAIN_NAMSPACE = "http://ontology.cf.ericsson.net/pddl_example/";
    public static String BLOCKSWORLD_DOMAIN_NAMSPACE_PREFIX = "bworld";

    public static String BLOCK_PATH = "block";
    public static String BLOCK_NAMESPACE = BLOCKSWORLD_DOMAIN_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String BLOCK_LOCALNAME = "block"; //localName of the rdfs:class the resource describes
    public static String BLOCK_TYPE = BLOCK_NAMESPACE + BLOCK_LOCALNAME; //fullname of the rdfs:class the resource describes
    public static String LOCATION_PATH = "location";
    public static String LOCATION_NAMESPACE = BLOCKSWORLD_DOMAIN_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String LOCATION_LOCALNAME = "location"; //localName of the rdfs:class the resource describes
    public static String LOCATION_TYPE = LOCATION_NAMESPACE + LOCATION_LOCALNAME; //fullname of the rdfs:class the resource describes
    public static String LOCATION_OR_BLOCK_PATH = "locationOrBlock";
    public static String LOCATION_OR_BLOCK_NAMESPACE = BLOCKSWORLD_DOMAIN_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String LOCATION_OR_BLOCK_LOCALNAME = "location-or-block"; //localName of the rdfs:class the resource describes
    public static String LOCATION_OR_BLOCK_TYPE = LOCATION_OR_BLOCK_NAMESPACE + LOCATION_OR_BLOCK_LOCALNAME; //fullname of the rdfs:class the resource describes
    public static String MOVE_PATH = "move";
    public static String MOVE_NAMESPACE = BLOCKSWORLD_DOMAIN_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String MOVE_LOCALNAME = "move"; //localName of the rdfs:class the resource describes
    public static String MOVE_TYPE = MOVE_NAMESPACE + MOVE_LOCALNAME; //fullname of the rdfs:class the resource describes
    public static String ON_PATH = "on";
    public static String ON_NAMESPACE = BLOCKSWORLD_DOMAIN_NAMSPACE; //namespace of the rdfs:class the resource describes
    public static String ON_LOCALNAME = "on"; //localName of the rdfs:class the resource describes
    public static String ON_TYPE = ON_NAMESPACE + ON_LOCALNAME; //fullname of the rdfs:class the resource describes
}
