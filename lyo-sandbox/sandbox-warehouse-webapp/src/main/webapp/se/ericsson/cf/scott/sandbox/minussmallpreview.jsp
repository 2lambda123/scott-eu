<!DOCTYPE html>
<%--
 Copyright (c) 2011, 2012, 2017 IBM Corporation and others.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 and Eclipse Distribution License v. 1.0 which accompanies this distribution.

 The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 and the Eclipse Distribution License is available at
 http://www.eclipse.org/org/documents/edl-v10.php.

 Contributors:

  Sam Padgett     - initial API and implementation
  Michael Fiedler - adapted for OSLC4J
  Jad El-khoury   - initial implementation of code generator (422448)
  Frédéric Loiret - Switch the template to Bootstrap (519699)

 This file is generated by org.eclipse.lyo.oslc4j.codegenerator
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page import="org.eclipse.lyo.oslc4j.core.model.ServiceProvider"%>
<%@page import="java.util.List" %>
<%@page import="se.ericsson.cf.scott.sandbox.resources.Minus"%>
<%--
Start of user code imports
--%>
<%--
End of user code
--%>

<%@ page contentType="text/html" language="java" pageEncoding="UTF-8" %>

<%
  Minus aMinus = (Minus) request.getAttribute("aMinus");
%>
<%--
Start of user code getRequestAttributes
--%>
<%--
End of user code
--%>


<%--
Start of user code "indexBody"
--%>
<%--
TODO: Replace/adjust this default content as necessary.
All manual changes in this "protected" user code area will NOT be overwritten upon subsequent code generations.
To revert to the default generated content, delete all content in this file, and then re-generate.
--%>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title><%= aMinus.toString(false) %></title>

  <link href="<c:url value="/static/css/bootstrap-4.0.0-beta.min.css"/>" rel="stylesheet">
  <link href="<c:url value="/static/css/adaptor.css"/>" rel="stylesheet">

  <script src="<c:url value="/static/js/jquery-3.2.1.min.js"/>"></script>
  <script src="<c:url value="/static/js/popper-1.11.0.min.js"/>"></script>
  <script src="<c:url value="/static/js/bootstrap-4.0.0-beta.min.js"/>"></script>
</head>

<body>

<!-- Begin page content -->
<div>
        <div>
          <dl class="dl-horizontal">
            <dt>leftPredicate</dt>
            <dd><%= aMinus.leftPredicateToHtml()%></dd>
          </dl>
          <dl class="dl-horizontal">
            <dt>rightPredicate</dt>
            <dd><%= aMinus.rightPredicateToHtml()%></dd>
          </dl>
        </div>
      </div>
</body>
</html>
<%--
End of user code
--%>