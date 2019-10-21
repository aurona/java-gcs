<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.pgcs.search.GCSController" %>
<html>
<head>
  <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
  <title>Hello App Engine Standard Java 8</title>
</head>
<body>
    <h1>Cloud Search tests</h1>

  <table>
    <tr>
      <td colspan="2" style="font-weight:bold;">Available Tests:</td>
    </tr>
    <tr>
      <td><a href='/hello?order=getschema'>Get Schema</a></td>
      <td><a href='/hello?order=updateschema'>update Schema</a></td>
    </tr>
  </table>

</body>
</html>
