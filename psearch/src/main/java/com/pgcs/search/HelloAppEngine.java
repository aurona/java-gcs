package com.pgcs.search;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

//*** PHS: Servlet HelloAppEngine for /hello ***
@WebServlet(name = "HelloAppEngine", value = "/hello")
public class HelloAppEngine extends HttpServlet {
  private static final Logger log = Logger.getLogger(HelloAppEngine.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Properties properties = System.getProperties();

    log.info("PHS: HelloAppEngine servlet INFO IN");

    // PHS: Test 1: Call to static Query::Test()
    String ret = Query.Test();

    // PHS: Test 2: Call to PSchema::getSchema(dataSourceId)
    String dataSourceId = "c1daac23a76ec19dde99fa6eaa35e3da";
    PSchema schema = new PSchema();
    String schemastr = schema.getSchema(dataSourceId);

    // PHS: Just print some results from previous tests.
    response.setContentType("text/plain");
    response.getWriter().println("Hello App Engine - Standard using "
        + SystemProperty.version.get() + " Java " + properties.get("java.specification.version")
        + " RESPONSE: "
        + "\n Test 1: " + ret
        + "\n Test 2: " + schemastr);
  }

  public static String getInfo() {
    return "Version: " + System.getProperty("java.version")
          + " OS: " + System.getProperty("os.name")
          + " User: " + System.getProperty("user.name");
  }

} // Of HelloAppEngine

