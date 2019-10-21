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
import com.google.enterprise.cloudsearch.sdk.CredentialFactory;
import com.google.enterprise.cloudsearch.sdk.indexing.IndexingService.RequestMode;
//import com.google.enterprise.cloudsearch.sdk.indexing.util.Uploader.UploaderHelper;

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
        String schemastr = "{ \"fruit\": \"Apple\", \"size\": \"Large\", \"color\": \"Red\" }";

        try {
            CloudSearch cloudSearch = buildAuthorizedClient();
            String resourceName = String.format("datasources/%s", dataSourceId);

            // Prepare the new schema: class UpdateSchemaRequest
            // Original: UploadRequest.UpdateSchemaRequest updateRequest = new UploadRequest.UpdateSchemaRequest();
            UpdateSchemaRequest updateRequest = new UpdateSchemaRequest();

            Operation operation = cloudSearch.indexing().datasources().updateSchema(resourceName, updateRequest).execute();
            GCSUtils.log("AFTER updateSchema:execute");
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to get schema: " + e.getDetails());
        } catch (IOException e) {
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
