package com.pgcs.search;

// PHS: REST API Calls
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCSREST {


    // This is just a REST API call.
    public static String sendPostRequest(String requestUrl, String payload) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null)
            {
                jsonString.append(line);
                System.out.println(line);
            }
            br.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString() ;

    } // end sendPostRequest

    
    /**
    * Performs a Query to the index using the REST API directly
    * @param searchQuery Search query to be made.
    */
    public String restSearch(String searchQuery) {
        GCSUtils.log("GCSSchema: restSearch start");

        String result = "REST Query API call didn't return anything";

        //String payload = "{\"field\":\"project_id\",\"value\":[\"TCGA-LAML\"]}";
        //String requestUrl="https://api.gdc.cancer.gov/cases";
        String payload = "{\"requestOptions\":{\"searchApplicationId\":\"searchapplications/default\"},\"" + searchQuery + "\":\"gcs\"}";
        String requestUrl="https://cloudsearch.googleapis.com/v1/query/search";
        GCSUtils.log("GCSSchema: restSearch Input: " + payload);

        result = sendPostRequest(requestUrl, payload);
   
        GCSUtils.log("GCSSchema: restSearch result: " + result);

        return result;

    } // end restSearch



} // end class
