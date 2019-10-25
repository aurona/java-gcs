package com.pgcs.search;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// *** Servlet HelloAppEngine for /gcs ***
@WebServlet(name = "GCSController", value = "/gcs")
public class GCSController extends HttpServlet {
	private static final long serialVersionUID = 1L; // serializable warning

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

            // Only used for Unit Testing now
            String result = "GCSController Test OK";
            response.setContentType("text/plain");
            response.getWriter().println(result);
    } // end doGet   

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String result = "";

        // If we received params, override these values
        String datasourceid = request.getParameter("datasourceid");
        GCSUtils.log("GCSController: SOURCE ID test 1: " + datasourceid);

        datasourceid = request.getParameterValues("datasourceid")[0];
        GCSUtils.log("GCSController: SOURCE ID test 2: " + datasourceid);

        if (datasourceid == null) datasourceid = "";

        String schemastr = request.getParameter("schema");
        if (schemastr == null) schemastr = "";

        GCSUtils.log("GCSController: SOURCE ID: " + datasourceid);
        GCSUtils.log("GCSController: SCHEMA FILE PATH: " + schemastr);

        // Parameters in call URL: We decide which action to perform
        String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSController: Order: " + order);

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();

        // Depending on the order, we make the call
        switch (order) {
            case "getschema":
                result = gcsschema.getSchema(datasourceid);
                GCSUtils.log("AFTER getSchema: " + result);
                break;
            case "updateschema":
                result = gcsschema.updateSchema(datasourceid, schemastr);
                GCSUtils.log("AFTER updateSchema: " + result);
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println("Order: " + order
                                   + "Result:\n\n" + result);

    } // end doGet

} // Of GCSController