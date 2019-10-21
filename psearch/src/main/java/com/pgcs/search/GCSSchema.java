package com.pgcs.search;

import java.io.BufferedReader;
import java.io.FileReader;
// PHS: Other imports
import java.io.IOException;
import java.util.Collections;

// PHS: cloud search api imports
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.model.Operation;
import com.google.api.services.cloudsearch.v1.model.Schema;
import com.google.api.services.cloudsearch.v1.model.Status;
import com.google.api.services.cloudsearch.v1.model.UpdateSchemaRequest;


public class GCSSchema {

    public static final int OPERATION_POLL_INTERVAL = 3 * 1000;


    /**
    * Retrieves the schema for a datasource.
    * @param dataSourceId Unique ID of the datasource.
    */
    public String getSchema(String dataSourceId) {
        String schemastr = "";

        try {
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);
            Schema schema = cloudSearch.indexing().datasources().getSchema(resourceName).execute();
            GCSUtils.log("AFTER getSchema:execute");
            schemastr = schema.toPrettyString();
            GCSUtils.log("AFTER schema.toPrettyString:" + schemastr);
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to get schema: " + e.getDetails());
        } catch (IOException e) {
            System.err.println("Unable to get schema: " + e.getMessage());
        }
      
      return schemastr;

    } // end getSchema

    /**
    * Updates the schema of a datasource.
    * @param dataSourceId Unique ID of the datasource.
    * @param newSchema New JSON schema for the datasource.
    */
    public String updateSchema(String dataSourceId) {

        try {
            // Authenticating Service Account
            CloudSearch cloudSearch = buildAuthorizedClient();

            String resourceName = String.format("datasources/%s", dataSourceId);
            Schema schema;

            GCSUtils.log("UPDATE 1");
            try (BufferedReader br = new BufferedReader(new FileReader("./schema.json"))) {
                schema = cloudSearch.getObjectParser().parseAndClose(br, Schema.class);
            } // end try

            GCSUtils.log("UPDATE 2");

            UpdateSchemaRequest updateSchemaRequest  = new UpdateSchemaRequest().setSchema(schema);

            GCSUtils.log("UPDATE 3");

            Operation operation = cloudSearch.indexing().datasources()
                .updateSchema(resourceName, updateSchemaRequest)
                .execute();
            GCSUtils.log("AFTER updateSchema:execute");

            // This Operation is not syncronous. We have to wait and see when it finishes
            while (operation.getDone() == null || operation.getDone() == false) {
                // Wait before polling again
                Thread.sleep(OPERATION_POLL_INTERVAL);
                operation = cloudSearch.operations().get(operation.getName()).execute();
            } // end while

            // Operation is complete, check result
            Status error = operation.getError();
            if (error != null) {
                System.err.println("Error updating schema:" + error.getMessage());
            } else {
                System.out.println("Schema updated.");
            }


        } catch (GoogleJsonResponseException e) {
        System.err.println("Unable to update schema: " + e.getDetails());
        } catch (IOException e) {
        System.err.println("Unable to update schema: " + e.getMessage());
        } catch (InterruptedException e) {
        System.err.println("Interrupted while waiting for schema update: "
            + e.getMessage());
        }

      return "whatever";

    } // end getSchema



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
  }

} // end class
