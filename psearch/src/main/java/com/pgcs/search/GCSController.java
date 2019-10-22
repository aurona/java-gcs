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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String result = "";

        GCSUtils.log("GCSController: Loading configuration");
        String sourceId = "c1daac23a76ec19da2795a7d778054c8";
        String localSchema = "demoschema.json";

        GCSUtils.log("GCSController: SOURCE ID: " + sourceId);
        GCSUtils.log("GCSController: LOCAL SCHEMA: " + localSchema);
    
        // TEST
        String [] args = new String[1];
        args[0] = "-Dconfig=WEB-INF/classes/psearch.properties";
        Configuration.initConfig(args);
        ConfigValue<String> sourceIdconf = Configuration.getString("api.sourceId", null);
        //ConfigValue<String> localSchemaconf = Configuration.getString("demo.schema", null);

        GCSUtils.log("GCSController: SOURCE ID 2: " + sourceIdconf.get());

        // PHS: When fixed from configuration file, remember to add .get() to these two variables
        // Confirm that we have read the needed configuration
        if (sourceId == null) {
            throw new IllegalArgumentException("Missing api.sourceId value in configuration");
        }
            if (localSchema == null) {
            throw new IllegalArgumentException("Missing demo.schema value in configuration");
        }

        // Parameters in call URL
        String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSController: Order: " + order);

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();

        // Depending on the order, we make the call
        switch (order) {
            case "getschema":
                result = gcsschema.getSchema(sourceId);
                GCSUtils.log("AFTER getSchema: " + result);
                break;
            case "updateschema":
                result = gcsschema.updateSchema(sourceId);
                GCSUtils.log("AFTER updateSchema: " + result);
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println("GCSController response [GCSController Test OK]");

    } // end doGet

} // Of GCSController

