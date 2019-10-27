package com.pgcs.search;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Proxy 'gcs' for parameters encoded in URL 
@WebServlet(name = "GCSController", value = "/gcs")
public class GCSController extends HttpServlet {
	private static final long serialVersionUID = 1L; // serializable warning

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

            // Converting goGet to doPost
            // doPost(request, response);

            // Only used for Unit Testing now
            String result = "GCSController Test OK";
            response.setContentType("text/plain");
            response.getWriter().println(result);
    } // end doGet   

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String result = "";

        // If we received params, override these values. I am not checking if they are null... should I?
        String datasourceid = request.getParameter("datasourceid"); // This is also valid: datasourceid = request.getParameterValues("datasourceid")[0];
        String schemastr = request.getParameter("schema");

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
                break;
            case "updateschemajson":
                result = gcsschema.updateSchemaJSON(datasourceid, schemastr);
                break;
            case "test":
                result = "PARAMS: " + datasourceid + " - " + schemastr; // gcsschema.test(datasourceid, schemastr);
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println(result);

    } // end doPost

} // Of GCSController