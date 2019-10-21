package com.pgcs.search;

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

public class GCSSchema {

    /**
    * Retrieves the schema for a datasource.
    * @param dataSourceId Unique ID of the datasource.
    */
    public String getSchema(String dataSourceId) {
        String schemastr = "";

        try {
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);
            GCSUtils.log("getSchema:ResourceName: " + resourceName);
            Schema schema = cloudSearch.indexing().datasources().getSchema(resourceName).execute();
            GCSUtils.log("AFTER getSchema:ResourceName: ");
            schemastr = schema.toPrettyString();
            GCSUtils.log("AFTER schema.toPrettyString");
        } catch (GoogleJsonResponseException e) {
            GCSUtils.log("paso 6");
            System.err.println("Unable to get schema: " + e.getDetails());
        } catch (IOException e) {
            GCSUtils.log("paso 7");
            System.err.println("Unable to get schema: " + e.getMessage());
        }
      
      return schemastr;

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
