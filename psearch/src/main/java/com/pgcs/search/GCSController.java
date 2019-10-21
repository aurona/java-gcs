package com.pgcs.search;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//*** PHS: Servlet HelloAppEngine for /hello ***
@WebServlet(name = "GCSController", value = "/hello")
public class GCSController extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Properties properties = System.getProperties(); // PHS: Only for testing

    // Parameters in call URL
    String param1 = request.getParameter("param");
 

    GCSUtils.log("PHS: HelloAppEngine servlet INFO TEST WITH static class");

    // PHS: Test Call to GCSSchema::getSchema(dataSourceId)
    String dataSourceId = "c1daac23a76ec19dde99fa6eaa35e3da";
    GCSSchema schema = new GCSSchema();
    String schemastr = schema.getSchema(dataSourceId);

    // PHS: Just print some results from previous tests.
    response.setContentType("text/plain");
    response.getWriter().println("GCSController response [GCSController Test OK]: " + schemastr);
    // For JUnit testing. If includes 'GCSController Test OK', test result will be ok

  } // end doGet


} // Of GCSController

