        // GCSController: Read Configuration from file 
        /* PHS: Won't not read file:
        Properties properties = new Properties();
        properties.setProperty("config", "psearch.properties"); // In command line it would be '-Dconfig=psearch.properties'
        Configuration.initConfig(properties);
        ConfigValue<String> sourceId = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchema = Configuration.getString("demo.schema", null);
        */
        
        /* PHS: Passing argv[] workd locally but don't deploy files to AppEngine:
        String [] args = new String[1];
        args[0] = "-Dconfig=psearch.properties";
        Configuration.initConfig(args);
        ConfigValue<String> sourceIdconf = Configuration.getString("api.sourceId", null);
        ConfigValue<String> localSchemaconf = Configuration.getString("demo.schema", null);
        */
