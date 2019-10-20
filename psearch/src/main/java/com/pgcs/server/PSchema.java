package com.pgcs.server;

// PHS: cloud search api imports
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.services.cloudsearch.v1.CloudSearch;
import com.google.api.services.cloudsearch.v1.model.Operation;
import com.google.api.services.cloudsearch.v1.model.Schema;
import com.google.api.services.cloudsearch.v1.model.Status;
import com.google.api.services.cloudsearch.v1.model.UpdateSchemaRequest;

// PHS: Other imports
import java.io.IOException;
import java.util.Collections;

public class PSchema {

    /**
    * Retrieves the schema for a datasource.
    * @param dataSourceId Unique ID of the datasource.
    */
    public void getSchema(String dataSourceId) {
        try {
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);
            Schema schema = cloudSearch.indexing().datasources().getSchema(resourceName).execute();
            System.out.println(schema.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to delete schema: " + e.getDetails());
        } catch (IOException e) {
            System.err.println("Unable to delete schema: " + e.getMessage());
        }
      
    } // Of getSchema

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
        .setApplicationName("Cloud Search Samples")
        .build();
  }

} // Of class
