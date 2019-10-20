package com.pgcs.search;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// PHS: My demo server classes
import com.pgcs.server.Query;
import com.pgcs.server.PSchema;

//*** PHS: Servlet HelloAppEngine for /hello ***
@WebServlet(name = "HelloAppEngine", value = "/hello")
public class HelloAppEngine extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Properties properties = System.getProperties();

    // PHS: Test 1: Call to static Query::Test()
    String ret = Query.Test();

    // PHS: Test 2: Call to PSchema::getSchema(dataSourceId)
    String dataSourceId = "c1daac23a76ec19dde99fa6eaa35e3da";
    PSchema schema = new PSchema();
    schema.getSchema(dataSourceId);

    // PHS: Just print some results from previous tests.
    response.setContentType("text/plain");
    response.getWriter().println("Hello App Engine - Standard using "
        + SystemProperty.version.get() + " Java " + properties.get("java.specification.version")
        + " RESPONSE: " + ret);
  }

  public static String getInfo() {
    return "Version: " + System.getProperty("java.version")
          + " OS: " + System.getProperty("os.name")
          + " User: " + System.getProperty("user.name");
  }

} // Of HelloAppEngine

