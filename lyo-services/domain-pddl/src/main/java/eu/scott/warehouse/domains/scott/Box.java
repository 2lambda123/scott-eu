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
 *       Sam Padgett          - initial API and implementation
 *     Michael Fiedler      - adapted for OSLC4J
 *     Jad El-khoury        - initial implementation of code generator (422448)
 *     Matthieu Helleboid   - Support for multiple Service Providers.
 *     Anass Radouani       - Support for multiple Service Providers.
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package eu.scott.warehouse.domains.scott;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcMemberProperty;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRepresentation;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.Representation;
import org.eclipse.lyo.oslc4j.core.model.ValueType;
import org.eclipse.lyo.oslc4j.core.model.ResourceShape;
import org.eclipse.lyo.oslc4j.core.model.ResourceShapeFactory;

import eu.scott.warehouse.domains.scott.ScottDomainConstants;


import eu.scott.warehouse.domains.scott.ScottDomainConstants;
import eu.scott.warehouse.domains.scott.Coord;
import eu.scott.warehouse.domains.scott.Coord;
import eu.scott.warehouse.domains.scott.ConveyorBelt;
import eu.scott.warehouse.domains.scott.Robot;
import eu.scott.warehouse.domains.scott.Shelf;

// Start of user code imports
// End of user code

// Start of user code preClassCode
// End of user code

// Start of user code classAnnotations
// End of user code
@OslcNamespace(ScottDomainConstants.BOX_NAMESPACE)
@OslcName(ScottDomainConstants.BOX_LOCALNAME)
@OslcResourceShape(title = "Box Resource Shape", describes = ScottDomainConstants.BOX_TYPE)
public class Box
    extends AbstractResource
    implements IBox
{
    // Start of user code attributeAnnotation:atX
    // End of user code
    private Link atX;
    // Start of user code attributeAnnotation:atY
    // End of user code
    private Link atY;
    // Start of user code attributeAnnotation:onBelt
    // End of user code
    private Link onBelt;
    // Start of user code attributeAnnotation:onShelf
    // End of user code
    private Link onShelf;
    // Start of user code attributeAnnotation:onRobot
    // End of user code
    private Link onRobot;
    // Start of user code attributeAnnotation:isFree
    // End of user code
    private Boolean isFree;
    
    // Start of user code classAttributes
    // End of user code
    // Start of user code classMethods
    // End of user code
    public Box()
    {
        super();
    
        // Start of user code constructor1
        // End of user code
    }
    
    public Box(final URI about)
    {
        super(about);
    
        // Start of user code constructor2
        // End of user code
    }
    
    public static ResourceShape createResourceShape() throws OslcCoreApplicationException, URISyntaxException {
        return ResourceShapeFactory.createResourceShape(OSLC4JUtils.getServletURI(),
        OslcConstants.PATH_RESOURCE_SHAPES,
        ScottDomainConstants.BOX_PATH,
        Box.class);
    }
    
    
    public String toString()
    {
        return toString(false);
    }
    
    public String toString(boolean asLocalResource)
    {
        String result = "";
        // Start of user code toString_init
        // End of user code
    
        if (asLocalResource) {
            result = result + "{a Local Box Resource} - update Box.toString() to present resource as desired.";
            // Start of user code toString_bodyForLocalResource
            // End of user code
        }
        else {
            result = String.valueOf(getAbout());
        }
    
        // Start of user code toString_finalize
        // End of user code
    
        return result;
    }
    
    
    // Start of user code getterAnnotation:atX
    // End of user code
    @OslcName("at-x")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "at-x")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({ScottDomainConstants.COORD_TYPE})
    @OslcReadOnly(false)
    public Link getAtX()
    {
        // Start of user code getterInit:atX
        // End of user code
        return atX;
    }
    
    // Start of user code getterAnnotation:atY
    // End of user code
    @OslcName("at-y")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "at-y")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({ScottDomainConstants.COORD_TYPE})
    @OslcReadOnly(false)
    public Link getAtY()
    {
        // Start of user code getterInit:atY
        // End of user code
        return atY;
    }
    
    // Start of user code getterAnnotation:onBelt
    // End of user code
    @OslcName("on-belt")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "on-belt")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({ScottDomainConstants.CONVEYORBELT_TYPE})
    @OslcReadOnly(false)
    public Link getOnBelt()
    {
        // Start of user code getterInit:onBelt
        // End of user code
        return onBelt;
    }
    
    // Start of user code getterAnnotation:onShelf
    // End of user code
    @OslcName("on-shelf")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "on-shelf")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({ScottDomainConstants.SHELF_TYPE})
    @OslcReadOnly(false)
    public Link getOnShelf()
    {
        // Start of user code getterInit:onShelf
        // End of user code
        return onShelf;
    }
    
    // Start of user code getterAnnotation:onRobot
    // End of user code
    @OslcName("on-robot")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "on-robot")
    @OslcOccurs(Occurs.ZeroOrOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({ScottDomainConstants.ROBOT_TYPE})
    @OslcReadOnly(false)
    public Link getOnRobot()
    {
        // Start of user code getterInit:onRobot
        // End of user code
        return onRobot;
    }
    
    // Start of user code getterAnnotation:isFree
    // End of user code
    @OslcName("is-free")
    @OslcPropertyDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE + "is-free")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Boolean)
    @OslcReadOnly(false)
    public Boolean isIsFree()
    {
        // Start of user code getterInit:isFree
        // End of user code
        return isFree;
    }
    
    
    // Start of user code setterAnnotation:atX
    // End of user code
    public void setAtX(final Link atX )
    {
        // Start of user code setterInit:atX
        // End of user code
        this.atX = atX;
    
        // Start of user code setterFinalize:atX
        // End of user code
    }
    
    // Start of user code setterAnnotation:atY
    // End of user code
    public void setAtY(final Link atY )
    {
        // Start of user code setterInit:atY
        // End of user code
        this.atY = atY;
    
        // Start of user code setterFinalize:atY
        // End of user code
    }
    
    // Start of user code setterAnnotation:onBelt
    // End of user code
    public void setOnBelt(final Link onBelt )
    {
        // Start of user code setterInit:onBelt
        // End of user code
        this.onBelt = onBelt;
    
        // Start of user code setterFinalize:onBelt
        // End of user code
    }
    
    // Start of user code setterAnnotation:onShelf
    // End of user code
    public void setOnShelf(final Link onShelf )
    {
        // Start of user code setterInit:onShelf
        // End of user code
        this.onShelf = onShelf;
    
        // Start of user code setterFinalize:onShelf
        // End of user code
    }
    
    // Start of user code setterAnnotation:onRobot
    // End of user code
    public void setOnRobot(final Link onRobot )
    {
        // Start of user code setterInit:onRobot
        // End of user code
        this.onRobot = onRobot;
    
        // Start of user code setterFinalize:onRobot
        // End of user code
    }
    
    // Start of user code setterAnnotation:isFree
    // End of user code
    public void setIsFree(final Boolean isFree )
    {
        // Start of user code setterInit:isFree
        // End of user code
        this.isFree = isFree;
    
        // Start of user code setterFinalize:isFree
        // End of user code
    }
    
    
}
