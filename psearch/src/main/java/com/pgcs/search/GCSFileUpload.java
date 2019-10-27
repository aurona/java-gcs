package com.pgcs.search;

import java.io.IOException;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// *** Servlet for /upload ***
@WebServlet(name = "GCSFileUpload", value = "/upload")
@MultipartConfig
public class GCSFileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L; // serializable warning

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String result = "";

        // If we received params, override these values. I am not checking if they are null... should I?
        String datasourceid = request.getParameter("datasourceid"); // This is also valid: datasourceid = request.getParameterValues("datasourceid")[0];
        String schemastr = request.getParameter("schema");

        GCSUtils.log("GCSFileUpload: SOURCE ID: " + datasourceid);
        GCSUtils.log("GCSFileUpload: SCHEMA FILE PATH: " + schemastr);

        // Parameters in call URL: We decide which action to perform
        String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSFileUpload: Order: " + order);

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();

        // Depending on the order, we make the call
        switch (order) {
            case "updateschemafile":
                result = gcsschema.updateSchemaFile(datasourceid, schemastr);
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

} // Of GCSFileUpload