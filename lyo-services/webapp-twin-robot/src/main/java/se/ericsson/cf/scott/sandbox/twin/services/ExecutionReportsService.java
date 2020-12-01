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
 *     Jim Amsden          - Support for UI Preview (494303)
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package se.ericsson.cf.scott.sandbox.twin.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.UriBuilder;

import org.apache.wink.json4j.JSONObject;
import org.eclipse.lyo.oslc4j.provider.json4j.JsonHelper;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;

import se.ericsson.cf.scott.sandbox.twin.TwinManager;
import se.ericsson.cf.scott.sandbox.twin.TwinConstants;
import eu.scott.warehouse.domains.scott.ScottDomainConstants;
import eu.scott.warehouse.domains.scott.ScottDomainConstants;
import se.ericsson.cf.scott.sandbox.twin.servlet.ServiceProviderCatalogSingleton;
import eu.scott.warehouse.domains.scott.ActionExecutionReport;
import eu.scott.warehouse.domains.scott.ExecutableAction;

// Start of user code imports
// End of user code

// Start of user code pre_class_code
// End of user code
@OslcService(ScottDomainConstants.SCOTT_WAREHOUSE_DOMAIN)
@Path("twins/{twinKind}/{twinId}/service2/actionExecutionReports")
public class ExecutionReportsService
{
    @Context private HttpServletRequest httpServletRequest;
    @Context private HttpServletResponse httpServletResponse;
    @Context private UriInfo uriInfo;

    // Start of user code class_attributes
    // End of user code

    // Start of user code class_methods
    // End of user code

    public ExecutionReportsService()
    {
        super();
    }

    private void addCORSHeaders (final HttpServletResponse httpServletResponse) {
        //UI preview can be blocked by CORS policy.
        //add select CORS headers to every response that is embedded in an iframe.
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
    }

    @GET
    @Path("{actionExecutionReportId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_JSON_LD, OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ActionExecutionReport getActionExecutionReport(
                @PathParam("twinKind") final String twinKind, @PathParam("twinId") final String twinId, @PathParam("actionExecutionReportId") final String actionExecutionReportId
        ) throws IOException, ServletException, URISyntaxException
    {
        // Start of user code getResource_init
        // End of user code

        final ActionExecutionReport aActionExecutionReport = TwinManager.getActionExecutionReport(httpServletRequest, twinKind, twinId, actionExecutionReportId);

        if (aActionExecutionReport != null) {
            // Start of user code getActionExecutionReport
            // End of user code
            httpServletResponse.addHeader(TwinConstants.HDR_OSLC_VERSION, TwinConstants.OSLC_VERSION_V2);
            return aActionExecutionReport;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{actionExecutionReportId}")
    @Produces({ MediaType.TEXT_HTML })
    public Response getActionExecutionReportAsHtml(
        @PathParam("twinKind") final String twinKind, @PathParam("twinId") final String twinId, @PathParam("actionExecutionReportId") final String actionExecutionReportId
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getActionExecutionReportAsHtml_init
        // End of user code

        final ActionExecutionReport aActionExecutionReport = TwinManager.getActionExecutionReport(httpServletRequest, twinKind, twinId, actionExecutionReportId);

        if (aActionExecutionReport != null) {
            httpServletRequest.setAttribute("aActionExecutionReport", aActionExecutionReport);
            // Start of user code getActionExecutionReportAsHtml_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/se/ericsson/cf/scott/sandbox/twin/actionexecutionreport.jsp");
            rd.forward(httpServletRequest,httpServletResponse);
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{actionExecutionReportId}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML})
    public Compact getActionExecutionReportCompact(
        @PathParam("twinKind") final String twinKind, @PathParam("twinId") final String twinId, @PathParam("actionExecutionReportId") final String actionExecutionReportId
        ) throws ServletException, IOException, URISyntaxException
    {
        String iconUri = OSLC4JUtils.getPublicURI() + "/images/ui_preview_icon.gif";
        String smallPreviewHintHeight = "10em";
        String smallPreviewHintWidth = "45em";
        String largePreviewHintHeight = "20em";
        String largePreviewHintWidth = "45em";

        // Start of user code getActionExecutionReportCompact_init
        //TODO: adjust the preview height & width values from the default values provided above.
        // End of user code

        final ActionExecutionReport aActionExecutionReport = TwinManager.getActionExecutionReport(httpServletRequest, twinKind, twinId, actionExecutionReportId);

        if (aActionExecutionReport != null) {
            final Compact compact = new Compact();

            compact.setAbout(aActionExecutionReport.getAbout());
            compact.setTitle(aActionExecutionReport.toString());

            compact.setIcon(new URI(iconUri));

            //Create and set attributes for OSLC preview resource
            final Preview smallPreview = new Preview();
            smallPreview.setHintHeight(smallPreviewHintHeight);
            smallPreview.setHintWidth(smallPreviewHintWidth);
            smallPreview.setDocument(UriBuilder.fromUri(aActionExecutionReport.getAbout()).path("smallPreview").build());
            compact.setSmallPreview(smallPreview);

            final Preview largePreview = new Preview();
            largePreview.setHintHeight(largePreviewHintHeight);
            largePreview.setHintWidth(largePreviewHintWidth);
            largePreview.setDocument(UriBuilder.fromUri(aActionExecutionReport.getAbout()).path("largePreview").build());
            compact.setLargePreview(largePreview);

            httpServletResponse.addHeader(TwinConstants.HDR_OSLC_VERSION, TwinConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            return compact;
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{actionExecutionReportId}/smallPreview")
    @Produces({ MediaType.TEXT_HTML })
    public void getActionExecutionReportAsHtmlSmallPreview(
        @PathParam("twinKind") final String twinKind, @PathParam("twinId") final String twinId, @PathParam("actionExecutionReportId") final String actionExecutionReportId
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getActionExecutionReportAsHtmlSmallPreview_init
        // End of user code

        final ActionExecutionReport aActionExecutionReport = TwinManager.getActionExecutionReport(httpServletRequest, twinKind, twinId, actionExecutionReportId);

        if (aActionExecutionReport != null) {
            httpServletRequest.setAttribute("aActionExecutionReport", aActionExecutionReport);
            // Start of user code getActionExecutionReportAsHtmlSmallPreview_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/se/ericsson/cf/scott/sandbox/twin/actionexecutionreportsmallpreview.jsp");
            httpServletResponse.addHeader(TwinConstants.HDR_OSLC_VERSION, TwinConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            rd.forward(httpServletRequest, httpServletResponse);
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("{actionExecutionReportId}/largePreview")
    @Produces({ MediaType.TEXT_HTML })
    public void getActionExecutionReportAsHtmlLargePreview(
        @PathParam("twinKind") final String twinKind, @PathParam("twinId") final String twinId, @PathParam("actionExecutionReportId") final String actionExecutionReportId
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getActionExecutionReportAsHtmlLargePreview_init
        // End of user code

        final ActionExecutionReport aActionExecutionReport = TwinManager.getActionExecutionReport(httpServletRequest, twinKind, twinId, actionExecutionReportId);

        if (aActionExecutionReport != null) {
            httpServletRequest.setAttribute("aActionExecutionReport", aActionExecutionReport);
            // Start of user code getActionExecutionReportAsHtmlLargePreview_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/se/ericsson/cf/scott/sandbox/twin/actionexecutionreportlargepreview.jsp");
            httpServletResponse.addHeader(TwinConstants.HDR_OSLC_VERSION, TwinConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            rd.forward(httpServletRequest, httpServletResponse);
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }
}