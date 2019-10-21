package com.pgcs.search;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// *** Servlet HelloAppEngine for /hello ***
@WebServlet(name = "GCSController", value = "/hello")
public class GCSController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        Properties properties = System.getProperties(); // PHS: Only for testing
        String result = "";

        // Parameters in call URL
        String order = request.getParameter("order");
        if (order == null) order = "test";
        GCSUtils.log("GCSController: Order: " + order);

        // Depending on the order, we make the call
        switch (order) {
            case "getschema":
                result = getSchema();
                GCSUtils.log("AFTER getSchema: " + result);
                break;
            case "updateschema":
                result = updateSchema();
                GCSUtils.log("AFTER updateSchema: " + result);
                break;
            default:
                break;
        } // end switch

        response.setContentType("text/plain");
        response.getWriter().println("GCSController response [GCSController Test OK]");

    } // end doGet

    private String getSchema() {
        String dataSourceId = "c1daac23a76ec19da2795a7d778054c8"; // Test
        GCSSchema schema = new GCSSchema();
        return schema.getSchema(dataSourceId);
    } // end getSchema

    private String updateSchema() {
        String dataSourceId = "c1daac23a76ec19da2795a7d778054c8"; // Test
        GCSSchema schema = new GCSSchema();
        return schema.updateSchema(dataSourceId);
    } // end updateSchema

} // Of GCSController

