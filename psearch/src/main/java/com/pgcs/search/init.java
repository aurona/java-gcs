package com.pgcs.search;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection; // PHS: For connecting to Cloud SQL.
import java.sql.DriverManager; // PHS: For connecting to Cloud SQL.
import java.sql.SQLException; // PHS: For connecting to Cloud SQL.

//*** PHS: Servlet init for /init ***
@SuppressWarnings("serial")
@WebServlet(name = "init", value="/init")
public class init extends HttpServlet {

    // PHS: Init method to create Cloud SQL connection 
    @Override
    public void init() throws ServletException {
        Connection conn; // Cloud SQL connection. PHS: Esto va aquí?

        // Cloud SQL table creation commands
        final String createContentTableSql =
            "CREATE TABLE IF NOT EXISTS posts ( post_id INT NOT NULL "
                + "AUTO_INCREMENT, author_id INT NOT NULL, timestamp DATETIME NOT NULL, "
                + "title VARCHAR(256) NOT NULL, "
                + "body VARCHAR(1337) NOT NULL, PRIMARY KEY (post_id) )";

        final String createUserTableSql =
            "CREATE TABLE IF NOT EXISTS users ( user_id INT NOT NULL "
                + "AUTO_INCREMENT, user_fullname VARCHAR(64) NOT NULL, "
                + "PRIMARY KEY (user_id) )";

        try {
            String url = System.getProperty("cloudsql");

            try {
                conn = DriverManager.getConnection(url);

                // Create the tables so that the SELECT query doesn't throw an exception
                // if the user visits the page before any posts have been added

                conn.createStatement().executeUpdate(createContentTableSql); // create content table
                conn.createStatement().executeUpdate(createUserTableSql); // create user table

            } catch (SQLException e) {
                throw new ServletException("Unable to connect to SQL server", e);
            }

        } finally {
            // Nothing really to do here.
        }
    } // PHS: Of init

} // Of CreateBlogPost
