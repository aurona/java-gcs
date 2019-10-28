package com.pgcs.search;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Proxy 'upload' for parameters encoded in URL 
@WebServlet(name = "GCSFileUpload", value = "/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
                 maxFileSize = 1024 * 1024 * 25, // 25 MB
                 maxRequestSize = 1024 * 1024 * 50) // 50 MB
public class GCSFileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L; // serializable warning

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String result = "";

        String order        = request.getParameter("order");
        String datasourceid = request.getParameter("datasourceid");
        String schemajson   = request.getParameter("schemajson");
        String schemafile   = request.getParameter("schemafile");
        //final Part filePart = request.getPart("file");

        GCSUtils.log("GCSFileUpload: ORDER: " + order);
        GCSUtils.log("GCSFileUpload: SOURCE ID: " + datasourceid);
        GCSUtils.log("GCSFileUpload: SCHEMA FILE JSON: " + schemajson);
        GCSUtils.log("GCSFileUpload: SCHEMA FILE CONTENT: " + schemafile);

        // Parameters in call URL: We decide which action to perform
        //String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSFileUpload: Order: " + order);

        // Instance of ourown GCSSchema class with methods to manipulate Schema in DataSource
        GCSSchema gcsschema = new GCSSchema();

        // Depending on the order, we make the call
        switch (order) {
            case "updateschemafile":
                result = gcsschema.updateSchemaFile(datasourceid, schemafile);
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

} // Of GCSFileUpload