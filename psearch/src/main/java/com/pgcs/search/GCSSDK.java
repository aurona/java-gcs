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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.CloudSearchScopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
// PHS: cloud search api imports
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.util.Utils;

import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.model.Operation;
import com.google.api.services.cloudsearch.v1.model.RequestOptions;
import com.google.api.services.cloudsearch.v1.model.Schema;
import com.google.api.services.cloudsearch.v1.CloudSearch.Query.Search; // https://developers.google.com/resources/api-libraries/documentation/cloudsearch/v1/java/latest/com/google/api/services/cloudsearch/v1/CloudSearch.Query.Search.html#Search-com.google.api.services.cloudsearch.v1.model.SearchRequest-
import com.google.api.services.cloudsearch.v1.model.SearchRequest; // https://developers.google.com/resources/api-libraries/documentation/cloudsearch/v1/java/latest/com/google/api/services/cloudsearch/v1/model/class-use/SearchRequest.html
import com.google.api.services.cloudsearch.v1.model.Status;
import com.google.api.services.cloudsearch.v1.model.UpdateSchemaRequest;

public class GCSSDK {

    /** Path to the Service Account's Private Key file */
    private static final String SERVICE_ACCOUNT_FILE_PATH = "keys/pgcs-java-61730cf74a3a.json";

    public static final int OPERATION_POLL_INTERVAL = 3 * 1000;

    /**
    * Retrieves the schema for a datasource.
    * @param dataSourceId Unique ID of the datasource.
    */
    public String getSchema(String dataSourceId) {
        GCSUtils.log("GCSSDK: getSchema start");

        String schemastr = "";

        try {
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);
            Schema schema = cloudSearch.indexing().datasources().getSchema(resourceName).execute();
            schemastr = schema.toPrettyString();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to get schema: " + e.getDetails());
        } catch (IOException e) {
            System.err.println("Unable to get schema: " + e.getMessage());
        }
      
        return schemastr;

    } // end getSchema


    /**
    * Updates the schema of a datasource from a file containing the JSON definition
    * @param dataSourceId Unique ID of the datasource.
    * @param newSchema New JSON schema for the datasource.
    */
    public String updateSchemaFile(String dataSourceId, String schemastr) {
        GCSUtils.log("GCSSDK: updateSchemaFile start");

        String result = "updateSchemaFile";
        
        try {
            // Authenticating Service Account
            CloudSearch cloudSearch = buildAuthorizedClient();

            String resourceName = String.format("datasources/%s", dataSourceId);

            // Load the Schema from a string received
            Schema schema;
            schema = cloudSearch.getJsonFactory().fromString(schemastr, Schema.class);
            UpdateSchemaRequest updateSchemaRequest  = new UpdateSchemaRequest().setSchema(schema);

            Operation operation = cloudSearch.indexing().datasources()
                .updateSchema(resourceName, updateSchemaRequest)
                .execute();

            // This Operation is not syncronous. We have to wait and see when it finishes
            while (operation.getDone() == null || operation.getDone() == false) {
                // Wait before polling again
                Thread.sleep(OPERATION_POLL_INTERVAL);
                operation = cloudSearch.operations().get(operation.getName()).execute();
            } // end while

            // Operation is complete, check result
            Status error = operation.getError();
            if (error != null) {
                result = "Error updating schema:" + error.getMessage();
                System.err.println(result);
            } else {
                result = "Schema Updated. Execute Get Schema to check";
                System.out.println(result);
            }

        } catch (GoogleJsonResponseException e) {
            result = "EXCEPTION GoogleJsonResponseException: Unable to update schema: " + e.getDetails();
            System.err.println(result);
        } catch (IOException e) {
            result = "EXCEPTION IOException: Unable to update schema: " + e.getMessage();
            System.err.println(result);
        } catch (InterruptedException e) {
            result = "EXCEPTION: Interrupted while waiting for schema update: " + e.getMessage();
            System.err.println(result);
        }

        return result;

    } // end updateSchemaFile


    /**
    * Deletes the schema of a datasource
    * @param dataSourceId Unique ID of the datasource.
    */
    public String deleteSchema(String dataSourceId) {
        GCSUtils.log("GCSSDK: deleteSchema start");

        String result = "Schema deleted. Use Get Schema to verify";

        try {
            // Authenticating Service Account
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);

            Operation operation = cloudSearch.indexing().datasources()
                .deleteSchema(resourceName)
                .execute();

            // This Operation is not syncronous. We have to wait and see when it finishes
            while (operation.getDone() == null || operation.getDone() == false) {
                // Wait before polling again
                Thread.sleep(OPERATION_POLL_INTERVAL);
                operation = cloudSearch.operations().get(operation.getName()).execute();
            } // end while

            // Operation is complete, check result
            Status error = operation.getError();
            if (error != null) {
                result = "Error updating schema:" + error.getMessage();
                System.err.println(result);
            } else {
                result = "Schema Deleted. Execute Get Schema to check";
                System.out.println(result);
            }

        } catch (GoogleJsonResponseException e) {
            result = "EXCEPTION GoogleJsonResponseException: Unable to delete schema: " + e.getDetails();
            System.err.println(result);
        } catch (IOException e) {
            result = "EXCEPTION IOException: Unable to delete schema: " + e.getMessage();
            System.err.println(result);
        } catch (InterruptedException e) {
            result = "EXCEPTION: Interrupted while waiting for schema delete: " + e.getMessage();
            System.err.println(result);
        }

        GCSUtils.log("GCSSDK: deleteSchema result: " + result);

        return result;

    } // end deleteSchema


    /**
    * Performs a Query to the index using the REST API directly
    * @param searchQuery Search query to be made.
    */
    public String sdkSearch(String searchQuery) {
        GCSUtils.log("GCSSDK: sdkSearch start");
        String results = "";
        
        try {
            GCSUtils.log("GCSSDK: sdkSearch in try");
            CloudSearch cloudSearch = getCloudSearchAPIService("pablohs@gcloudsearch.com");
            GCSUtils.log("GCSSDK: sdkSearch after getCloudSearchAPIService");

            // We create the base Query object with the query string
            SearchRequest sr = new SearchRequest();
            sr.setQuery(searchQuery); // It can accept lots of parameters
            GCSUtils.log("GCSSDK: sdkSearch step 1");
            
            // Create the Request Options part of the JSON call
            RequestOptions ro = new RequestOptions();
            ro.setSearchApplicationId("searchapplications/default");
            sr.setRequestOptions(ro);
            GCSUtils.log("GCSSDK: sdkSearch step 2");
            
            // Execute the Search and get the results
            Search res = cloudSearch.query().search(sr); 
            GCSUtils.log("GCSSDK: sdkSearch step 3");
            Object o = res.execute();
            GCSUtils.log("GCSSDK: sdkSearch step 4");
        } catch (IOException e) {
            results = "GCSSDK: sdkSearch: IO Exception";
            System.err.println(results);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
       
        /* From Accenture - Intesa
        CloudSearch c = CloudSearchClient.getCloudSearchAPIService("cloud@cloudfirstitaly.com");//"service-account@isp-aem-gcs.iam.gserviceaccount.com");
        com.google.api.services.cloudsearch.v1.model.SearchRequest search = new com.google.api.services.cloudsearch.v1.model.SearchRequest();
        search.setDataSourceRestrictions(request.getDataSourceRestrictions());
        search.setFacetOptions(request.getFacetOptions());
        search.setPageSize(request.getPageSize());
        search.setQuery(request.getQuery());
        search.setQueryInterpretationOptions(request.getQueryInterpretationOptions());
        search.setRequestOptions(request.getRequestOptions());
        search.setSortOptions(request.getSortOptions());
        search.setStart(request.getStart());
        RequestOptions opt = new RequestOptions();
        opt.setSearchApplicationId("searchapplications/77f24de307e6d0634bb2c9803ddcfb3b");
        search.setQuery("comunicati");
        search.setRequestOptions(opt);
        */

        return results;

    } // end sdkSearch



    public String test(String dataSourceId, String schemastr) {
        String result = "test";
        GCSUtils.log("GCSSDK ***TEST***: Schema File: " + schemastr);
        
        try {
            Schema schema;
            CloudSearch cloudSearch = buildAuthorizedClient();
            schema = cloudSearch.getJsonFactory().fromString("{\"objectDefinitions\":[{\"name\":\"movie\"},{\"name\":\"person\"}]}", Schema.class);
            GCSUtils.log("GCSSDK: ***TEST***: " + schema.toPrettyString());
            result = schema.toPrettyString();
        } catch (GoogleJsonResponseException e) {
            result = "EXCEPTION GoogleJsonResponseException: Unable to TEST: " + e.getDetails();
            System.err.println(result);
        } catch (IOException e) {
            result = "EXCEPTION IOException: Unable to update schema: " + e.getMessage();
            System.err.println(result);
        }

        return result;

    } // end test



    /**
    * Builds and initializes the client with service account credentials.
    * @return CloudSearch instance
    * @throws IOException if unable to load credentials
    */
    private CloudSearch buildAuthorizedClient() throws IOException {
        // Get the service account credentials based on the GOOGLE_APPLICATION_CREDENTIALS
        // environment variable
        GoogleCredential credential = GoogleCredential.getApplicationDefault(
            Utils.getDefaultTransport(),
            Utils.getDefaultJsonFactory());
        // Ensure credentials have the correct scope
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(Collections.singletonList(
                "https://www.googleapis.com/auth/cloud_search"));
        }

        // Build the cloud search client
        return new CloudSearch.Builder(
            Utils.getDefaultTransport(),
            Utils.getDefaultJsonFactory(),
            credential)
            .setApplicationName("default") // PHS: Name of Search Application with access to DataSource
            .build();
    } // end buildAuthorizedClient


    /**
     * Build and return a Cloud Search service object authorized with the service
     * account that acts on behalf of the given user.
     *
     * @param userEmail The email of the user to impersonate. Needs permissions to access Cloud Search.
     * @return CloudSearch service object that is ready to make requests.
     */
    public static CloudSearch getCloudSearchAPIService(String userEmail)
        throws FileNotFoundException, IOException {
        GCSUtils.log("GCSSDK getCloudSearchAPIService start: email: " + userEmail);

        // This does not work from AppEngine, only from Standalone Java: FileInputStream credsFile = new FileInputStream(SERVICE_ACCOUNT_FILE_PATH);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream credsFile = classLoader.getResourceAsStream(SERVICE_ACCOUNT_FILE_PATH); // File name does not have the initial '/'. It is the file name who would be stores in WEB-INF/classes
        
        GoogleCredential init = GoogleCredential.fromStream(credsFile);

        HttpTransport httpTransport = init.getTransport();
        JsonFactory jsonFactory = init.getJsonFactory();

        GCSUtils.log("GCSSDK getCloudSearchAPIService file: " + credsFile.toString());
        GCSUtils.log("GCSSDK getCloudSearchAPIService httpTransport: " + init.getTransport());
        GCSUtils.log("GCSSDK getCloudSearchAPIService jsonFactory: " + init.getJsonFactory());
        GCSUtils.log("GCSSDK getCloudSearchAPIService getServiceAccountId: " + init.getServiceAccountId());
        GCSUtils.log("GCSSDK getCloudSearchAPIService getServiceAccountPrivateKey: " + init.getServiceAccountPrivateKey());
        GCSUtils.log("GCSSDK getCloudSearchAPIService CloudSearchScopes.CLOUD_SEARCH: " + CloudSearchScopes.CLOUD_SEARCH);
        GCSUtils.log("GCSSDK getCloudSearchAPIService userEmail: " + userEmail);

        GoogleCredential creds = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(init.getServiceAccountId())
            .setServiceAccountPrivateKey(init.getServiceAccountPrivateKey())
            .setServiceAccountScopes(Collections.singleton(CloudSearchScopes.CLOUD_SEARCH))
            .setServiceAccountUser(userEmail)
            .build();

        CloudSearch service = new CloudSearch.Builder(httpTransport, jsonFactory, creds)
            .setApplicationName("default") // Is the Application name relevant?
            .build();

        return service;
    }


} // end class