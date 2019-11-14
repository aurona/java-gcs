package com.pgcs.search;

// PHS: REST API Calls
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// PHS: Needed for G Suite domain-wide delegation of authority
import java.util.Collections;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.CloudSearchScopes;


public class GCSREST {

    /** Path to the Service Account's Private Key file */
    private static final String SERVICE_ACCOUNT_FILE_PATH = "/pgcs-java-5a18a03f4bfe.json";

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


    /**
     * Build and return a Cloud Search service object authorized with the service
     * account that acts on behalf of the given user.
     *
     * @param userEmail The email of the user to impersonate. Needs permissions to access Cloud Search.
     * @return CloudSearch service object that is ready to make requests.
     */
    public static CloudSearch getCloudSearchAPIService(String userEmail)
        throws FileNotFoundException, IOException {

    FileInputStream credsFile = new FileInputStream(SERVICE_ACCOUNT_FILE_PATH);

    GoogleCredential init = GoogleCredential.fromStream(credsFile);

    HttpTransport httpTransport = init.getTransport();
    JsonFactory jsonFactory = init.getJsonFactory();

    GoogleCredential creds = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(jsonFactory)
        .setServiceAccountId(init.getServiceAccountId())
        .setServiceAccountPrivateKey(init.getServiceAccountPrivateKey())
        .setServiceAccountScopes(Collections.singleton(CloudSearchScopes.CLOUD_SEARCH))
        .setServiceAccountUser(userEmail)
        .build();

    CloudSearch service = new CloudSearch.Builder(httpTransport, jsonFactory, creds).build();

    return service;
    }


} // end class
