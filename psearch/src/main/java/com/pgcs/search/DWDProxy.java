package com.pgcs.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

// PHS: Needed for G Suite domain-wide delegation of authority: 
import java.util.Collections;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// PHS: cloud search api imports
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.CloudSearchScopes;
import com.google.api.services.cloudsearch.v1.model.RequestOptions;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.services.cloudsearch.v1.model.Operation;
import com.google.api.services.cloudsearch.v1.model.Schema;
import com.google.api.services.cloudsearch.v1.CloudSearch.Query.Search; // https://developers.google.com/resources/api-libraries/documentation/cloudsearch/v1/java/latest/com/google/api/services/cloudsearch/v1/CloudSearch.Query.Search.html#Search-com.google.api.services.cloudsearch.v1.model.SearchRequest-
import com.google.api.services.cloudsearch.v1.model.SearchRequest; // https://developers.google.com/resources/api-libraries/documentation/cloudsearch/v1/java/latest/com/google/api/services/cloudsearch/v1/model/class-use/SearchRequest.html
import com.google.api.services.cloudsearch.v1.model.SearchResponse;
import com.google.api.services.cloudsearch.v1.model.Status;
import com.google.api.services.cloudsearch.v1.model.UpdateSchemaRequest;

// PHS: New google-auth-library for Credentials
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.auth.Credentials;
import com.google.auth.appengine.AppEngineCredentials;

public class DWDProxy {

    /** Path to the Service Account's Private Key file */
    private static final String SERVICE_ACCOUNT_FILE_PATH = "keys/pgcs-java-key.json";
    private static final String USER_TO_IMPERSONATE = "pablohs@gcloudsearch.com";

    /**
    * Performs a Query to the index using the REST API directly
    * @param searchQuery Search query to be made.
    */
    public String sdkSearch(String searchQuery) {
        GCSUtils.log("GCSSDK: sdkSearch start");
        String results = "";
        
        try {
            GCSUtils.log("GCSSDK: sdkSearch in try");
            CloudSearch cloudSearch = getCloudSearchAPIService(USER_TO_IMPERSONATE);
            GCSUtils.log("GCSSDK: sdkSearch after getCloudSearchAPIService");

            // We create the base Query object with the query string
            SearchRequest sr = new SearchRequest();
            sr.setQuery(searchQuery); // It can accept lots of parameters
            GCSUtils.log("GCSSDK: sdkSearch step 1");
            
            // Create the Request Options part of the JSON call
            RequestOptions ro = new RequestOptions();
            ro.setSearchApplicationId("searchapplications/c1daac23a76ec19dffb5c6d9010a14be");
            sr.setRequestOptions(ro);
            GCSUtils.log("GCSSDK: sdkSearch step 2");
            
            // Execute the Search and get the results
            Search srch = cloudSearch.query().search(sr); 
            GCSUtils.log("GCSSDK: sdkSearch step 3");
            SearchResponse res = srch.execute();
            GCSUtils.log("GCSSDK: sdkSearch result: " + res.toPrettyString());
            results = res.toPrettyString();
        } catch (IOException e) {
            results = "GCSSDK: sdkSearch: IO Exception";
            System.err.println(results);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
       
        return results;

    } // end sdkSearch


    /**
     * Build and return a Cloud Search service object authorized with the service
     * account that acts on behalf of the given user.
     *
     * @param userEmail The email of the user to impersonate. Needs permissions to access Cloud Search.
     * @return CloudSearch service object that is ready to make requests.
     */
    public static CloudSearch getCloudSearchAPIService(String userToImpersonate)
        throws FileNotFoundException, IOException {
        GCSUtils.log("GCSSDK getCloudSearchAPIService start: email: " + userToImpersonate);

        // This does not work from AppEngine, only from Standalone Java: FileInputStream credsFile = new FileInputStream(SERVICE_ACCOUNT_FILE_PATH);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream credsFile = classLoader.getResourceAsStream(SERVICE_ACCOUNT_FILE_PATH); // File name does not have the initial '/'. It is the file name who would be stores in WEB-INF/classes
        
        GoogleCredential init = GoogleCredential.fromStream(credsFile);

        HttpTransport httpTransport = init.getTransport();
        JsonFactory jsonFactory = init.getJsonFactory();

        GoogleCredential creds = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(init.getServiceAccountId())
            .setServiceAccountPrivateKey(init.getServiceAccountPrivateKey())
            .setServiceAccountScopes(Collections.singleton(CloudSearchScopes.CLOUD_SEARCH_QUERY))
            .setServiceAccountUser(userToImpersonate)
            .build();

        CloudSearch service = new CloudSearch.Builder(httpTransport, jsonFactory, creds)
            .setApplicationName("psearch 1.0-SNAPSHOT") // Is the Application name relevant?
            .build();

        return service;
    }



} // end class