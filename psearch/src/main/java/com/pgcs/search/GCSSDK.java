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

public class GCSSDK {

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
        GCSUtils.log("GCSSDK: updateSchemaFile start: datasource: " + dataSourceId);

        String result = "updateSchemaFile";
        
        try {
            // Authenticating Service Account
            CloudSearch cloudSearch = buildAuthorizedClient();
            GCSUtils.log("GCSSDK: updateSchemaFile step 1");

            String resourceName = String.format("datasources/%s", dataSourceId);
            GCSUtils.log("GCSSDK: updateSchemaFile resourceName: " + resourceName);

            // Load the Schema from a string received
            Schema schema;
            schema = cloudSearch.getJsonFactory().fromString(schemastr, Schema.class);
            GCSUtils.log("GCSSDK: updateSchemaFile step 2");

            UpdateSchemaRequest updateSchemaRequest  = new UpdateSchemaRequest().setSchema(schema);

            GCSUtils.log("GCSSDK: updateSchemaFile step 3");

            Operation operation = cloudSearch.indexing().datasources()
                .updateSchema(resourceName, updateSchemaRequest)
                .execute();
            GCSUtils.log("GCSSDK: updateSchemaFile step 4");

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
            .setApplicationName("default") // PHS: Name of Search Application with access to DataSource?
            .build();
    } // end buildAuthorizedClient



} // end class