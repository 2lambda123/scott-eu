// Start of user code Copyright
/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *	   Sam Padgett	       - initial API and implementation
 *     Michael Fiedler     - adapted for OSLC4J
 *     Jad El-khoury        - initial implementation of code generator (https://bugs.eclipse.org/bugs/show_bug.cgi?id=422448)
 *     Matthieu Helleboid   - Support for multiple Service Providers.
 *     Anass Radouani       - Support for multiple Service Providers.
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package se.ericsson.cf.scott.sandbox.whc;

import com.github.jsonldjava.utils.Obj;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContextEvent;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import se.ericsson.cf.scott.sandbox.whc.servlet.ServiceProviderCatalogSingleton;
import se.ericsson.cf.scott.sandbox.whc.ServiceProviderInfo;
import eu.scott.warehouse.domains.pddl.Action;
import eu.scott.warehouse.domains.pddl.Plan;
import eu.scott.warehouse.domains.pddl.Step;


// Start of user code imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import eu.scott.warehouse.domains.pddl.Action;
import eu.scott.warehouse.domains.pddl.Plan;
import eu.scott.warehouse.domains.pddl.Step;

import javax.servlet.ServletContext;
import org.eclipse.lyo.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scott.warehouse.domains.blocksworld.Move;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashSet;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.ARQException;
import org.apache.jena.util.ResourceUtils;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.IResource;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.store.StoreFactory;
import se.ericsson.cf.scott.sandbox.whc.trs.WhcChangeHistories;
import java.time.Duration;
import java.util.logging.Level;

import org.eclipse.lyo.oslc4j.trs.server.ChangeHistories;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.bridge.SLF4JBridgeHandler;
// End of user code

// Start of user code pre_class_code
// End of user code

public class WarehouseControllerManager {

    // Start of user code class_attributes
    private final static String PACKAGE_ROOT = WarehouseControllerManager.class.getPackage().getName();
    private final static Logger log = LoggerFactory.getLogger(WarehouseControllerManager.class);
    private static final String DEFAULT_SP_ID = "default";
    private static Store store;
    private static ServletContext context;
    private static ServiceProviderInfo serviceProviderInfo;

    // TODO Andrew@2018-02-26: use lock object if CRUD write ops will be allowed
    // FIXME Andrew@2018-02-26: this should be in the Lyo Store
    private static Map<String, Object[]> plans = new HashMap<>();
    private static WhcChangeHistories changeHistoriesInstance;
    // End of user code
    
    
    // Start of user code class_methods
    // region class_methods
    private static String parameterFQDN(final String s) {
        return PACKAGE_ROOT + "." + s;
    }

    private static String p(final String s) {
        return context.getInitParameter(parameterFQDN(s));
    }

    private static Model loadJenaModelFromResource(final String resourceName, final Lang lang) {
        final InputStream resourceAsStream = WarehouseControllerManager.class.getClassLoader().getResourceAsStream(
                resourceName);
        final Model problemModel = ModelFactory.createDefaultModel();
        RDFDataMgr.read(problemModel, resourceAsStream, lang);
        return problemModel;
    }

    private static Model planForProblem(final Model problemModel) {
        log.trace("Problem request\n{}", jenaModelToString(problemModel));
        try {
            final InputStream response = requestPlanManually(problemModel);
            final Model responsePlan = ModelFactory.createDefaultModel();
            RDFDataMgr.read(responsePlan, response, Lang.TURTLE);
            log.info("Plan response\n{}", jenaModelToString(responsePlan));
            return responsePlan;
        } catch (IOException e) {
            log.error("Something went wrong", e);
            throw new IllegalStateException(e);
        }
    }

    private static InputStream requestPlanManually(final Model problemModel) throws IOException {
        String url = "http://aide.md.kth.se:3020/planner/planCreationFactory";

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFDataMgr.write(out, problemModel, RDFFormat.TURTLE_BLOCKS);

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-type", "text/turtle");
        post.setHeader("Accept", "text/turtle");
        post.setEntity(new ByteArrayEntity(out.toByteArray()));

        HttpResponse response = client.execute(post);
        return response.getEntity().getContent();
    }

    private static String jenaModelToString(final Model responsePlan) {
        final StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, responsePlan, RDFFormat.TURTLE_PRETTY);
        return stringWriter.toString();
    }

    // TODO Andrew@2018-02-07: submit to the JenaModelHelper
    public static <T> T[] fromJenaModelTyped(final Model model, Class<T> clazz) {
        try {
            final Object[] objects = JenaModelHelper.fromJenaModel(model, clazz);
            //noinspection unchecked
            final T[] clazzObjects = (T[]) objects;
            return clazzObjects;
        } catch (DatatypeConfigurationException | IllegalAccessException | InvocationTargetException | InstantiationException | OslcCoreApplicationException
                | NoSuchMethodException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // TODO Andrew@2018-02-07: submit to the JenaModelHelper
    public static <T> T fromJenaModelSingle(final Model model, Class<T> clazz) {
        final T[] ts = fromJenaModelTyped(model, clazz);
        if (ts.length != 1) {
            throw new IllegalArgumentException(
                    "Model shall contain exactly 1 instance of the class");
        }
        return ts[0];
    }

    // TODO Andrew@2018-02-23: move to JMH
    // TODO Andrew@2018-02-23: create a stateful JMH that would keep resources hashed by URI
    private static <R extends IResource> R nav(final Model m, final Link l,
            final Class<R> rClass) {
        final R[] rs = fromJenaModelTyped(m, rClass);
        for (R r : rs) {
            if(l.getValue().equals(r.getAbout())) {
                return r;
            }
        }
        throw new IllegalArgumentException("Link cannot be followed in this model");
    }

    private static IResource navTry(final Model m, final Link l,
            final Class<? extends IResource>... rClass) {
        for (Class<? extends IResource> aClass : rClass) {
            try {
                final IResource nav = nav(m, l, aClass);
                return nav;
            } catch (IllegalArgumentException e) {
                log.warn("Fix RDFS reasoning in JMH!!!");
            }
        }
        // give up
        throw new IllegalArgumentException("Link cannot be followed in this model");
    }

    private static void skolemize(final Model m) {
        final ResIterator resIterator = m.listSubjects();
        while(resIterator.hasNext()) {
            final Resource resource = resIterator.nextResource();
            if(resource != null && resource.isAnon()) {
                final String skolemURI = "urn:skolem:" + resource.getId()
                        .getBlankNodeId()
                        .getLabelString();
                final Resource skolemizedResource = ResourceUtils.renameResource(resource,
                        skolemURI);
            }
        }
    }
    //endregion

    public static ChangeHistories getChangeHistories() {
        return changeHistoriesInstance;
    }


    // End of user code

    public static void contextInitializeServletListener(final ServletContextEvent servletContextEvent)
    {

        // Start of user code contextInitializeServletListener

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);

        context = servletContextEvent.getServletContext();
        try {
            store = StoreFactory.sparql(p("store.query"), p("store.update"));
            // TODO Andrew@2017-07-18: Remember to deactivate when switch to more persistent arch
            store.removeAll();
        } catch (IOException | ARQException | HttpException e) {
            log.error(
                    "SPARQL Store failed to initialise with the URIs query={};update={}",
                    p("store.query"), p("store.update"), e
            );
        }

        try {
            final String mqttBroker = p("trs.mqtt.broker");
            final String mqttTopic = p("trs.mqtt.topic");
            final MqttClient mqttClient = new MqttClient(mqttBroker, "WHC Controller Service");
            final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            // TODO Andrew@2018-03-13: set highest QoS
            mqttClient.connect(mqttConnectOptions);
            changeHistoriesInstance = new WhcChangeHistories(mqttClient,
                                                             mqttTopic,
                                                             Duration.ofMinutes(5).toMillis());
        } catch (MqttException e) {
            log.error("MQTT connection failed");
        }

        final Model problemModel = loadJenaModelFromResource("sample-problem-request.ttl",
                Lang.TURTLE);

        Model planModel = planForProblem(problemModel);
        skolemize(planModel);
        final Plan plan = fromJenaModelSingle(planModel, Plan.class);
        log.debug("Received plan: {}", plan);

        final ArrayList<IResource> planResources = new ArrayList<>();
        planResources.add(plan);
        // TODO Andrew@2018-02-23: why not getSteps?
        final HashSet<Step> planSteps = plan.getStep();
        for (Step step : planSteps) {
            step.setOrder((Integer)step.getExtendedProperties().getOrDefault(new QName("http://www.w3.org/ns/shacl#", "order"), null));
            final IResource action = navTry(planModel, step.getAction(), Action.class, Move.class);
            planResources.add(step);
            planResources.add(action);
            log.info("Step {}: {}", step.getOrder(), action);
        }

        // TODO Andrew@2018-02-26: extract method
        final String planId = String.valueOf(plans.size() + 1);
        plan.setAbout(WarehouseControllerResourcesFactory.constructURIForPlan(DEFAULT_SP_ID, planId));
        plans.put(planId, planResources.toArray(new Object[0]));
        changeHistoriesInstance.addResource(plan);

        /*++++++++++++++++++++++++++++
          TRS Consumer initialisation
         ++++++++++++++++++++++++++++*/

//        final TrsConsumerConfiguration consumerConfig = new TrsConsumerConfiguration("https://aide.md.kth.se/fuseki/trs-everywhere/sparql", "https://aide.md.kth.se/fuseki/trs-everywhere/update", null, null, new TRSHttpClient(), "trs-consumer-whc",
//                Executors.newSingleThreadScheduledExecutor());
//        final Collection<TrsProviderConfiguration> providerConfigs = Lists.asList(new TrsProviderConfiguration());
//        TrsConsumerUtils.loadTrsProviders(consumerConfig, providerConfigs);


        // TODO: check the hack from Yash
        // TODO Andrew@2018-02-23: here the plan needs to be put through lyo store update

        // End of user code
    }

    public static void contextDestroyServletListener(ServletContextEvent servletContextEvent)
    {

        // Start of user code contextDestroyed
        // TODO Implement code to shutdown connections to data backbone etc...
        // End of user code
    }

    public static ServiceProviderInfo[] getServiceProviderInfos(HttpServletRequest httpServletRequest)
    {
        ServiceProviderInfo[] serviceProviderInfos = {};

        // Start of user code "ServiceProviderInfo[] getServiceProviderInfos(...)"
        serviceProviderInfo = new ServiceProviderInfo();
        serviceProviderInfo.serviceProviderId = DEFAULT_SP_ID;
        serviceProviderInfo.name = "Default Service Provider";
        serviceProviderInfos = new ServiceProviderInfo[]{serviceProviderInfo};
        // End of user code
        return serviceProviderInfos;
    }



    public static Object[] getPlan(HttpServletRequest httpServletRequest, final String
            serviceProviderId, final String planId)
    {
        Object[] aResource = null;

        // Start of user code getPlan

        log.trace("getPlan({}, {}) called", serviceProviderId, planId);
        // minimal impl to get the TRS provider going
        if (serviceProviderId.equals(DEFAULT_SP_ID)) {
            if (plans.containsKey(planId)) {
                aResource = plans.get(planId);
                log.info("found a plan", aResource);
            } else {
                log.warn("a plan {} was not found", planId);
            }
        }

        // End of user code
        return aResource;
    }




    public static String getETagFromPlan(final Plan aResource)
    {
        String eTag = null;
        // Start of user code getETagFromPlan
        // TODO Implement code to return an ETag for a particular resource
        // End of user code
        return eTag;
    }

}
