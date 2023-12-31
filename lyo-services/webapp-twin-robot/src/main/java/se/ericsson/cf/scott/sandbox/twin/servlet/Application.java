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
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     Jad El-khoury       - initial implementation of code generator (https://bugs.eclipse.org/bugs/show_bug.cgi?id=422448)
 *     Matthieu Helleboid  - Support for multiple Service Providers.
 *     Anass Radouani      - Support for multiple Service Providers.
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package se.ericsson.cf.scott.sandbox.twin.servlet;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.AllowedValues;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.CreationFactory;
import org.eclipse.lyo.oslc4j.core.model.Dialog;
import org.eclipse.lyo.oslc4j.core.model.Error;
import org.eclipse.lyo.oslc4j.core.model.ExtendedError;
import org.eclipse.lyo.oslc4j.core.model.OAuthConfiguration;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.Property;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.QueryCapability;
import org.eclipse.lyo.oslc4j.core.model.ResourceShape;
import org.eclipse.lyo.oslc4j.core.model.ResourceShapeFactory;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

import se.ericsson.cf.scott.sandbox.twin.services.ServiceProviderCatalogService;
import se.ericsson.cf.scott.sandbox.twin.services.IndependentServiceProviderService;
import se.ericsson.cf.scott.sandbox.twin.services.TwinsServiceProviderService;
import se.ericsson.cf.scott.sandbox.twin.services.ResourceShapeService;

import eu.scott.warehouse.domains.pddl.Action;
import eu.scott.warehouse.domains.scott.ActionExecutionReport;
import eu.scott.warehouse.domains.twins.DeviceRegistrationMessage;
import eu.scott.warehouse.domains.scott.ExecutableAction;
import eu.scott.warehouse.domains.pddl.Plan;
import eu.scott.warehouse.domains.twins.PlanExecutionRequest;
import eu.scott.warehouse.domains.pddl.Step;
import eu.scott.warehouse.domains.pddl.PddlDomainConstants;
import eu.scott.warehouse.domains.scott.ScottDomainConstants;
import eu.scott.warehouse.domains.twins.TwinsDomainConstants;
import se.ericsson.cf.scott.sandbox.twin.services.PlanExecutionService;
import se.ericsson.cf.scott.sandbox.twin.services.ExecutionReportsService;
import se.ericsson.cf.scott.sandbox.twin.services.IndependentServiceProviderService1;

// Start of user code imports
import org.eclipse.lyo.oslc4j.trs.server.service.TrackedResourceSetService;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import se.ericsson.cf.scott.sandbox.twin.xtra.factory.NaiveTrsFactories;
import se.ericsson.cf.scott.sandbox.twin.xtra.services.TwinAdminService;
// End of user code

// Start of user code pre_class_code
import java.util.Collections;

import org.eclipse.lyo.oslc4j.trs.server.PagedTrs;
import org.eclipse.lyo.oslc4j.trs.server.TrsEventHandler;
// End of user code

/**
 * Generated by Lyo Designer 4.0.0.201906191529
 */
public class Application extends javax.ws.rs.core.Application {

    private static final Set<Class<?>>         RESOURCE_CLASSES                          = new HashSet<Class<?>>();
    private static final Map<String, Class<?>> RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP = new HashMap<String, Class<?>>();

    // Start of user code class_attributes
    // End of user code

    // Start of user code class_methods

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(new NaiveTrsFactories.PagedTrsFactory()).to(PagedTrs.class);
                bindFactory(new NaiveTrsFactories.ResourceEventHandlerFactory()).to(
                    TrsEventHandler.class);
            }
        });
    }

    // End of user code

    static
    {
        RESOURCE_CLASSES.addAll(JenaProvidersRegistry.getProviders());
        RESOURCE_CLASSES.addAll(Json4JProvidersRegistry.getProviders());
        RESOURCE_CLASSES.add(PlanExecutionService.class);
        RESOURCE_CLASSES.add(ExecutionReportsService.class);
        RESOURCE_CLASSES.add(IndependentServiceProviderService1.class);

        // Catalog resources
        RESOURCE_CLASSES.add(ServiceProviderCatalogService.class);
        RESOURCE_CLASSES.add(IndependentServiceProviderService.class);
        RESOURCE_CLASSES.add(TwinsServiceProviderService.class);
        RESOURCE_CLASSES.add(ResourceShapeService.class);

        // Start of user code Custom Resource Classes
        // TODO Andrew@2018-05-27: UniversalResourceSingleProvider does not support returning arrays
        //  or collections
//        RESOURCE_CLASSES.add(UniversalResourceSingleProvider.class);
        RESOURCE_CLASSES.add(TrackedResourceSetService.class);

        RESOURCE_CLASSES.add(TwinAdminService.class);
        // End of user code

        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_ALLOWED_VALUES,           AllowedValues.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_COMPACT,                  Compact.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_CREATION_FACTORY,         CreationFactory.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_DIALOG,                   Dialog.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_ERROR,                    Error.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_EXTENDED_ERROR,           ExtendedError.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_OAUTH_CONFIGURATION,      OAuthConfiguration.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PREFIX_DEFINITION,        PrefixDefinition.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PREVIEW,                  Preview.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PROPERTY,                 Property.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PUBLISHER,                Publisher.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_QUERY_CAPABILITY,         QueryCapability.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_RESOURCE_SHAPE,           ResourceShape.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE,                  Service.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE_PROVIDER,         ServiceProvider.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE_PROVIDER_CATALOG, ServiceProviderCatalog.class);

        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(PddlDomainConstants.ACTION_PATH, Action.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(ScottDomainConstants.ACTIONEXECUTIONREPORT_PATH, ActionExecutionReport.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(TwinsDomainConstants.DEVICEREGISTRATIONMESSAGE_PATH, DeviceRegistrationMessage.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(ScottDomainConstants.EXECUTABLEACTION_PATH, ExecutableAction.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(PddlDomainConstants.PLAN_PATH, Plan.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(TwinsDomainConstants.PLANEXECUTIONREQUEST_PATH, PlanExecutionRequest.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(PddlDomainConstants.STEP_PATH, Step.class);
    }

    public Application()
           throws OslcCoreApplicationException,
                  URISyntaxException
    {
        final String BASE_URI = "http://localhost/validatingResourceShapes";
        for (final Map.Entry<String, Class<?>> entry : RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.entrySet()) {
            ResourceShapeFactory.createResourceShape(BASE_URI, OslcConstants.PATH_RESOURCE_SHAPES, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return RESOURCE_CLASSES;
    }

    public static Map<String, Class<?>> getResourceShapePathToResourceClassMap() {
        return RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP;
    }
}
