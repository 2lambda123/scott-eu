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
 *     Michael Fiedler      - Bugzilla adapter implementation
 *     Jad El-khoury        - initial implementation of code generator (https://bugs.eclipse.org/bugs/show_bug.cgi?id=422448)
 *     Matthieu Helleboid   - Allow Service Provider Factory class to be specific for each defined ServiceProvider
 *     Anass Radouani       - Allow Service Provider Factory class to be specific for each defined ServiceProvider
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package se.ericsson.cf.scott.sandbox.twin.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import javax.xml.namespace.QName;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderFactory;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;

import se.ericsson.cf.scott.sandbox.twin.TwinsServiceProviderInfo;

import eu.scott.warehouse.domains.RdfsDomainConstants;
import eu.scott.warehouse.domains.pddl.PddlDomainConstants;
import eu.scott.warehouse.domains.scott.ScottDomainConstants;
import eu.scott.warehouse.domains.twins.TwinsDomainConstants;
import se.ericsson.cf.scott.sandbox.twin.services.PlanExecutionService;
import se.ericsson.cf.scott.sandbox.twin.services.ExecutionReportsService;

// Start of user code imports

// End of user code

public class TwinsServiceProvidersFactory
{
    private static Class<?>[] RESOURCE_CLASSES =
    {
        PlanExecutionService.class, ExecutionReportsService.class
    };

    private TwinsServiceProvidersFactory()
    {
        super();
    }

    public static URI constructURI(final String twinKind, final String twinId)
    {
        String basePath = OSLC4JUtils.getServletURI();
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("twinKind", twinKind);

        pathParameters.put("twinId", twinId);
        String instanceURI = "twins/{twinKind}/{twinId}";

        final UriBuilder builder = UriBuilder.fromUri(basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }

    public static URI constructURI(final TwinsServiceProviderInfo serviceProviderInfo)
    {
        return constructURI(serviceProviderInfo.twinKind, serviceProviderInfo.twinId);
    }

    public static String constructIdentifier(final String twinKind, final String twinId)
    {
        return twinKind + "/" + twinId;
    }

    public static String constructIdentifier(final TwinsServiceProviderInfo serviceProviderInfo)
    {
        return constructIdentifier(serviceProviderInfo.twinKind, serviceProviderInfo.twinId);
    }

    public static ServiceProvider createServiceProvider(final TwinsServiceProviderInfo serviceProviderInfo)
            throws OslcCoreApplicationException, URISyntaxException, IllegalArgumentException {
        // Start of user code init
        // End of user code
        URI serviceProviderURI = constructURI(serviceProviderInfo);
        String identifier = constructIdentifier(serviceProviderInfo);
        String basePath = OSLC4JUtils.getServletURI();
        String title = serviceProviderInfo.name;
        String description = String.format("%s (id: %s; kind: %s)",
            "A Service Provider for Twins",
            identifier,
            "Twin SP");
        Publisher publisher = null;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("twinKind", serviceProviderInfo.twinKind);

        parameterMap.put("twinId", serviceProviderInfo.twinId);

        ServiceProvider serviceProvider = ServiceProviderFactory.createServiceProvider(basePath,
                                                    "",
                                                    title,
                                                    description,
                                                    publisher,
                                                    RESOURCE_CLASSES,
                                                    parameterMap);

        final PrefixDefinition[] prefixDefinitions =
        {
            new PrefixDefinition(OslcConstants.DCTERMS_NAMESPACE_PREFIX, new URI(OslcConstants.DCTERMS_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_CORE_NAMESPACE_PREFIX, new URI(OslcConstants.OSLC_CORE_NAMESPACE)),
            new PrefixDefinition(OslcConstants.OSLC_DATA_NAMESPACE_PREFIX, new URI(OslcConstants.OSLC_DATA_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDF_NAMESPACE_PREFIX, new URI(OslcConstants.RDF_NAMESPACE)),
            new PrefixDefinition(OslcConstants.RDFS_NAMESPACE_PREFIX, new URI(OslcConstants.RDFS_NAMESPACE)),
            new PrefixDefinition(RdfsDomainConstants.RDFS_NAMSPACE_PREFIX, new URI(RdfsDomainConstants.RDFS_NAMSPACE))
,
            new PrefixDefinition(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE_PREFIX, new URI(PddlDomainConstants.SCOTT_PDDL_2_1_SUBSET_SPEC_NAMSPACE))
,
            new PrefixDefinition(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE_PREFIX, new URI(ScottDomainConstants.SCOTT_WAREHOUSE_NAMSPACE))
,
            new PrefixDefinition(TwinsDomainConstants.TWINS_DOMAIN_NAMSPACE_PREFIX, new URI(TwinsDomainConstants.TWINS_DOMAIN_NAMSPACE))
        };
        serviceProvider.setPrefixDefinitions(prefixDefinitions);

        serviceProvider.setAbout(serviceProviderURI);
        serviceProvider.setIdentifier(identifier);
        serviceProvider.setCreated(new Date());
        serviceProvider.setDetails(new URI[] {serviceProviderURI});

        // Start of user code finalize
        final Map<QName, Object> properties = serviceProvider.getExtendedProperties();
        properties.put(QNames.trsNS("trackedResourceSet"), UriBuilder.fromUri(serviceProviderURI).path("trs").build());
        // End of user code
        return serviceProvider;
    }

}
