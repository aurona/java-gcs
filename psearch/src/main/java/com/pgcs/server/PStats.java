package com.pgcs.server;

//*** PHS: test for calling to Query API ***
public class PStats {

    static public String getStats() {
        Stats stats = new Stats();

        try {
            CloudSearch.Stats.Index idx = stats.getIndex();

        } catch IOException {
            return "error";
        } // PHS: Of Catch
                
        return "schema works";
      
    } // PHS: Of Test

} // Of Query class
