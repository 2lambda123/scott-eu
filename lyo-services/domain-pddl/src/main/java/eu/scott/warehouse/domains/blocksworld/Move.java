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

package eu.scott.warehouse.domains.blocksworld;

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

import eu.scott.warehouse.domains.blocksworld.BworldDomainConstants;
import eu.scott.warehouse.domains.pddl.Action;

import eu.scott.warehouse.domains.blocksworld.BworldDomainConstants;
import eu.scott.warehouse.domains.blocksworld.Block;
import eu.scott.warehouse.domains.blocksworld.LocationOrBlock;
import eu.scott.warehouse.domains.blocksworld.LocationOrBlock;

// Start of user code imports
import eu.scott.warehouse.domains.pddl.Action;
// End of user code

// Start of user code preClassCode
// End of user code

// Start of user code classAnnotations
// End of user code
@OslcNamespace(BworldDomainConstants.MOVE_NAMESPACE)
@OslcName(BworldDomainConstants.MOVE_LOCALNAME)
@OslcResourceShape(title = "move Resource Shape", describes = BworldDomainConstants.MOVE_TYPE)
public class Move
    extends Action
    implements IMove
{
    // Start of user code attributeAnnotation:moveB
    // End of user code
    private Link moveB;
    // Start of user code attributeAnnotation:moveX
    // End of user code
    private Link moveX;
    // Start of user code attributeAnnotation:moveY
    // End of user code
    private Link moveY;
    
    // Start of user code classAttributes
    // End of user code
    // Start of user code classMethods
    // End of user code
    public Move()
           throws URISyntaxException
    {
        super();
    
        // Start of user code constructor1
        // End of user code
    }
    
    public Move(final URI about)
           throws URISyntaxException
    {
        super(about);
    
        // Start of user code constructor2
        // End of user code
    }
    
    public static ResourceShape createResourceShape() throws OslcCoreApplicationException, URISyntaxException {
        return ResourceShapeFactory.createResourceShape(OSLC4JUtils.getServletURI(),
        OslcConstants.PATH_RESOURCE_SHAPES,
        BworldDomainConstants.MOVE_PATH,
        Move.class);
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
            result = result + "{a Local Move Resource} - update Move.toString() to present resource as desired.";
            // Start of user code toString_bodyForLocalResource
            // End of user code
        }
        else {
            result = getAbout().toString();
        }
    
        // Start of user code toString_finalize
        result = "Move{" + "block=" + moveB + ", from=" + moveX + ", to=" + moveY + '}';
        // End of user code
    
        return result;
    }
    
    
    // Start of user code getterAnnotation:moveB
    // End of user code
    @OslcName("move-b")
    @OslcPropertyDefinition(BworldDomainConstants.BLOCKSWORLD_DOMAIN_NAMSPACE + "move-b")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({BworldDomainConstants.BLOCK_TYPE})
    @OslcReadOnly(false)
    public Link getMoveB()
    {
        // Start of user code getterInit:moveB
        // End of user code
        return moveB;
    }
    
    // Start of user code getterAnnotation:moveX
    // End of user code
    @OslcName("move-x")
    @OslcPropertyDefinition(BworldDomainConstants.BLOCKSWORLD_DOMAIN_NAMSPACE + "move-x")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({BworldDomainConstants.LOCATION_OR_BLOCK_TYPE})
    @OslcReadOnly(false)
    public Link getMoveX()
    {
        // Start of user code getterInit:moveX
        // End of user code
        return moveX;
    }
    
    // Start of user code getterAnnotation:moveY
    // End of user code
    @OslcName("move-y")
    @OslcPropertyDefinition(BworldDomainConstants.BLOCKSWORLD_DOMAIN_NAMSPACE + "move-y")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({BworldDomainConstants.LOCATION_OR_BLOCK_TYPE})
    @OslcReadOnly(false)
    public Link getMoveY()
    {
        // Start of user code getterInit:moveY
        // End of user code
        return moveY;
    }
    
    
    // Start of user code setterAnnotation:moveB
    // End of user code
    public void setMoveB(final Link moveB )
    {
        // Start of user code setterInit:moveB
        // End of user code
        this.moveB = moveB;
    
        // Start of user code setterFinalize:moveB
        // End of user code
    }
    
    // Start of user code setterAnnotation:moveX
    // End of user code
    public void setMoveX(final Link moveX )
    {
        // Start of user code setterInit:moveX
        // End of user code
        this.moveX = moveX;
    
        // Start of user code setterFinalize:moveX
        // End of user code
    }
    
    // Start of user code setterAnnotation:moveY
    // End of user code
    public void setMoveY(final Link moveY )
    {
        // Start of user code setterInit:moveY
        // End of user code
        this.moveY = moveY;
    
        // Start of user code setterFinalize:moveY
        // End of user code
    }
    
    
}
