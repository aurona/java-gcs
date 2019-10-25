package com.pgcs.search;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.enterprise.cloudsearch.sdk.config.ConfigValue;
import com.google.enterprise.cloudsearch.sdk.config.Configuration;

// *** Servlet HelloAppEngine for /hello ***
@WebServlet(name = "GCSController", value = "/hello")
public class GCSController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String result = "";

        // Reading configuration file from WEB-INF/classes (deployed in AppEngine .war file)
        GCSUtils.log("GCSController: Loading configuration");
        String [] args = new String[1];
        args[0] = "-Dconfig=WEB-INF/classes/psearch.properties";
        Configuration.initConfig(args);
        ConfigValue<String> sourceId = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchema = Configuration.getString("demo.schema", null);

        GCSUtils.log("GCSController: SOURCE ID: " + sourceId.get());
        GCSUtils.log("GCSController: SCHEMA FILE PATH: " + localSchema.get());

        // Confirm that we have read the needed configuration
        if (sourceId.get() == null) {
            throw new IllegalArgumentException("Missing api.sourceId value in configuration");
        }
        if (localSchema.get() == null) {
            throw new IllegalArgumentException("Missing demo.schema value in configuration");
        }

        // Parameters in call URL: We decide which action to perform
        String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSController: Order: " + order);

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();

        // Depending on the order, we make the call
        switch (order) {
            case "getschema":
                result = gcsschema.getSchema(sourceId.get());
                GCSUtils.log("AFTER getSchema: " + result);
                break;
            case "updateschema":
                result = gcsschema.updateSchema(sourceId.get(), localSchema.get());
                GCSUtils.log("AFTER updateSchema: " + result);
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println(result);

    } // end doGet

} // Of GCSController