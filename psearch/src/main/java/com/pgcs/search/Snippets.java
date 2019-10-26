        /*
         * GCSController: Read Configuration from file 
        
        // PHS: Won't not read file:
        Properties properties = new Properties();
        properties.setProperty("config", "psearch.properties"); // In command line it would be '-Dconfig=psearch.properties'
        Configuration.initConfig(properties);
        ConfigValue<String> sourceId = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchema = Configuration.getString("demo.schema", null);
                
        // PHS: Passing argv[] workd locally but don't deploy files to AppEngine:
        String [] args = new String[1];
        args[0] = "-Dconfig=psearch.properties";
        Configuration.initConfig(args);
        ConfigValue<String> sourceIdconf = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchemaconf = Configuration.getString("demo.schema", null);
                
        // Reading configuration file from WEB-INF/classes (deployed in AppEngine .war file)
        GCSUtils.log("GCSController: Loading configuration");
        String [] args = new String[1];
        args[0] = "-Dconfig=WEB-INF/classes/psearch.properties";
        Configuration.initConfig(args);
        ConfigValue<String> sourceId = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchema = Configuration.getString("demo.schema", null);
        
        
        // Confirm that we have read the needed configuration
        if (sourceId.get() == null) {
            throw new IllegalArgumentException("Missing api.sourceId value in configuration");
        }
        if (localSchema.get() == null) {
            throw new IllegalArgumentException("Missing demo.schema value in configuration");
        }


        // Load the Schema from file in WEB-INF/classes
        Schema schema;
        try (BufferedReader br = new BufferedReader(new FileReader(schemaFilePath))) {
            schema = cloudSearch.getObjectParser().parseAndClose(br, Schema.class);
        }



        */