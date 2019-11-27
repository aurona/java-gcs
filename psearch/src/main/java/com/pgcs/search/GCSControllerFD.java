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

        // Instance of ourown GCSSDK class with methods to manipulate Schema in DataSource
        GCSSDK gcsSdk = new GCSSDK();
        GCSREST gcsRest = new GCSREST();
        DWDProxy dwdProxy = new DWDProxy();
        
        // Depending on the order, we make the call
        switch (order) {
            case "updateschemafile":
                result = gcsSdk.updateSchemaFile(datasourceid, schemafile);
                break;
            case "deleteschema":
                result = gcsSdk.deleteSchema(datasourceid);
                break;
            case "sdksearch":
                result = dwdProxy.sdkSearch(searchquery);
                break;
            case "restsearch":
                result = gcsRest.restSearch(searchquery);
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