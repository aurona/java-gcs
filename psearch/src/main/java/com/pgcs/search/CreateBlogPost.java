package com.pgcs.search;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//*** PHS: Servlet CreateBlogPost for /create ***
@SuppressWarnings("serial")
@WebServlet(name = "CreateBlogPost", value="/create")
public class CreateBlogPost extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        PrintWriter out = resp.getWriter();

        out.println(
            "Article with the title: " + req.getParameter("title") + " by "
                + req.getParameter("author") + " and the content: "
                + req.getParameter("description") + " added.");
    }

} // Of CreateBlogPost
