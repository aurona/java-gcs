package com.pgcs.search;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Proxy 'gcsfd' for parameters included in a FormData object (FD)
@WebServlet(name = "GCSControllerFD", value = "/gcsfd")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
                 maxFileSize = 1024 * 1024 * 25, // 25 MB
                 maxRequestSize = 1024 * 1024 * 50) // 50 MB
public class GCSControllerFD extends HttpServlet {
	private static final long serialVersionUID = 1L; // serializable warning

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String result = "";

        String order        = request.getParameter("order");
        String datasourceid = request.getParameter("datasourceid");
        String schemajson   = request.getParameter("schemajson");
        String schemafile   = request.getParameter("schemafile");
        String searchquery  = request.getParameter("searchquery");

        GCSUtils.log("GCSControllerFD: ORDER: " + order);
        GCSUtils.log("GCSControllerFD: SOURCE ID: " + datasourceid);
        GCSUtils.log("GCSControllerFD: SCHEMA FILE JSON: " + schemajson);
        GCSUtils.log("GCSControllerFD: SCHEMA FILE CONTENT: " + schemafile);
        GCSUtils.log("GCSControllerFD: SEARCH QUERY: " + searchquery);

        // Parameters in call URL: We decide which action to perform
        if (order == null) order = "test";

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();
        GCSREST gcsrest = new GCSREST();

        // Depending on the order, we make the call
        switch (order) {
            case "updateschemafile":
                result = gcsschema.updateSchemaFile(datasourceid, schemafile);
                break;
            case "deleteschema":
                result = gcsschema.deleteSchema(datasourceid);
                break;
            case "restsearch":
                result = gcsrest.restSearch(searchquery);
                break;
            case "test":
                result = "TEST: " + order + " / " + datasourceid + " / " + schemajson + " / " + schemafile;
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println(result);

    } // end doPost

} // Of GCSControllerFD