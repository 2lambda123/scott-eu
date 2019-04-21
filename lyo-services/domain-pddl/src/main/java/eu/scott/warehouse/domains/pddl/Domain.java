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

package eu.scott.warehouse.domains.pddl;

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

import eu.scott.warehouse.domains.pddl.PddlDomainConstants;


import eu.scott.warehouse.domains.RdfsDomainConstants;
import eu.scott.warehouse.domains.pddl.PddlDomainConstants;
import eu.scott.warehouse.domains.pddl.Action;
import eu.scott.warehouse.domains.pddl.Constant;
import eu.scott.warehouse.domains.pddl.EitherType;

// Start of user code imports
// End of user code

// Start of user code preClassCode
// End of user code

// Start of user code classAnnotations
// End of user code
@OslcNamespace(PddlDomainConstants.DOMAIN_NAMESPACE)
@OslcName(PddlDomainConstants.DOMAIN_LOCALNAME)
@OslcResourceShape(title = "Domain Resource Shape", describes = PddlDomainConstants.DOMAIN_TYPE)
public class Domain
    extends AbstractResource
    implements IDomain
{
    // Start of user code attributeAnnotation:action
    // End of user code
    private Link action;
    // Start of user code attributeAnnotation:constant
    // End of user code
    private Set<Link> constant = new HashSet<Link>();
    // Start of user code attributeAnnotation:function
    // End of user code
    private Set<Link> function = new HashSet<Link>();
    // Start of user code attributeAnnotation:predicate
    // End of user code
    private Set<Link> predicate = new HashSet<Link>();
    // Start of user code attributeAnnotation:type
    // End of user code
    private Link type;
    // Start of user code attributeAnnotation:label
    // End of user code
    private String label;
    
    // Start of user code classAttributes
    // End of user code
    // Start of user code classMethods
    // End of user code
    public Domain()
           throws URISyntaxException
    {
        super();
    
        // Start of user code constructor1
        // End of user code
    }
    
    public Domain(final URI about)
           throws URISyntaxException
    {
        super(about);
    
        // Start of user code constructor2
        // End of user code
    }
    
    public static ResourceShape createResourceShape() throws OslcCoreApplicationException, URISyntaxException {
        return ResourceShapeFactory.createResourceShape(OSLC4JUtils.getServletURI(),
        OslcConstants.PATH_RESOURCE_SHAPES,
        PddlDomainConstants.DOMAIN_PATH,
        Domain.class);
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
            result = result + "{a Local Domain Resource} - update Domain.toString() to present resource as desired.";
            // Start of user code toString_bodyForLocalResource
            // End of user code
        }
        else {
            result = getAbout().toString();
        }
    
        // Start of user code toString_finalize
        // End of user code
    
        return result;
    }
    
    public void addConstant(final Link constant)
    {
        this.constant.add(constant);
    }
    
    public void addFunction(final Link function)
    {
        this.function.add(function);
    }
    
    public void addPredicate(final Link predicate)
    {
        this.predicate.add(predicate);
    }
    
    
    // Start of user code getterAnnotation:action
    // End of user code
    @OslcName("action")
    @OslcPropertyDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE + "action")
    @OslcDescription("Action of the plan step.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({PddlDomainConstants.ACTION_TYPE})
    @OslcReadOnly(false)
    public Link getAction()
    {
        // Start of user code getterInit:action
        // End of user code
        return action;
    }
    
    // Start of user code getterAnnotation:constant
    // End of user code
    @OslcName("constant")
    @OslcPropertyDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE + "constant")
    @OslcDescription("Domain constants.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcValueType(ValueType.Resource)
    @OslcRange({PddlDomainConstants.CONSTANT_TYPE})
    @OslcReadOnly(false)
    public Set<Link> getConstant()
    {
        // Start of user code getterInit:constant
        // End of user code
        return constant;
    }
    
    // Start of user code getterAnnotation:function
    // End of user code
    @OslcName("function")
    @OslcPropertyDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE + "function")
    @OslcDescription("Domain functions.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcValueType(ValueType.Resource)
    @OslcReadOnly(false)
    public Set<Link> getFunction()
    {
        // Start of user code getterInit:function
        // End of user code
        return function;
    }
    
    // Start of user code getterAnnotation:predicate
    // End of user code
    @OslcName("predicate")
    @OslcPropertyDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE + "predicate")
    @OslcDescription("Domain predicates.")
    @OslcOccurs(Occurs.ZeroOrMany)
    @OslcValueType(ValueType.Resource)
    @OslcReadOnly(false)
    public Set<Link> getPredicate()
    {
        // Start of user code getterInit:predicate
        // End of user code
        return predicate;
    }
    
    // Start of user code getterAnnotation:type
    // End of user code
    @OslcName("type")
    @OslcPropertyDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE + "type")
    @OslcDescription("Parameter type.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.Resource)
    @OslcRange({PddlDomainConstants.EITHERTYPE_TYPE})
    @OslcReadOnly(false)
    public Link getType()
    {
        // Start of user code getterInit:type
        // End of user code
        return type;
    }
    
    // Start of user code getterAnnotation:label
    // End of user code
    @OslcName("label")
    @OslcPropertyDefinition(RdfsDomainConstants.RDFS_NAMSPACE + "label")
    @OslcDescription("Parameter name.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcValueType(ValueType.String)
    @OslcReadOnly(false)
    public String getLabel()
    {
        // Start of user code getterInit:label
        // End of user code
        return label;
    }
    
    
    // Start of user code setterAnnotation:action
    // End of user code
    public void setAction(final Link action )
    {
        // Start of user code setterInit:action
        // End of user code
        this.action = action;
    
        // Start of user code setterFinalize:action
        // End of user code
    }
    
    // Start of user code setterAnnotation:constant
    // End of user code
    public void setConstant(final Set<Link> constant )
    {
        // Start of user code setterInit:constant
        // End of user code
        this.constant.clear();
        if (constant != null)
        {
            this.constant.addAll(constant);
        }
    
        // Start of user code setterFinalize:constant
        // End of user code
    }
    
    // Start of user code setterAnnotation:function
    // End of user code
    public void setFunction(final Set<Link> function )
    {
        // Start of user code setterInit:function
        // End of user code
        this.function.clear();
        if (function != null)
        {
            this.function.addAll(function);
        }
    
        // Start of user code setterFinalize:function
        // End of user code
    }
    
    // Start of user code setterAnnotation:predicate
    // End of user code
    public void setPredicate(final Set<Link> predicate )
    {
        // Start of user code setterInit:predicate
        // End of user code
        this.predicate.clear();
        if (predicate != null)
        {
            this.predicate.addAll(predicate);
        }
    
        // Start of user code setterFinalize:predicate
        // End of user code
    }
    
    // Start of user code setterAnnotation:type
    // End of user code
    public void setType(final Link type )
    {
        // Start of user code setterInit:type
        // End of user code
        this.type = type;
    
        // Start of user code setterFinalize:type
        // End of user code
    }
    
    // Start of user code setterAnnotation:label
    // End of user code
    public void setLabel(final String label )
    {
        // Start of user code setterInit:label
        // End of user code
        this.label = label;
    
        // Start of user code setterFinalize:label
        // End of user code
    }
    
    
}
