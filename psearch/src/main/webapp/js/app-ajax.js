    <!-- Scripts to execute actions -->

    function getSchema(datasourceid) {
        var params = "order=getschema&datasourceid=" + datasourceid;
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                document.getElementById("res").innerHTML =
                this.responseText;
                }
        };
        xhttp.open("POST", "https://pgcs-java.appspot.com/gcs", true);
        xhttp.send(params);
    }

    function updateSchema(datasourceid, schemastr) {
        var params = "order=updateschema&datasourceid=" + datasourceid + "&schema=" + schemastr;
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                document.getElementById("res").innerHTML =
                this.responseText;
                }
        };
        xhttp.open("POST", "https://pgcs-java.appspot.com/gcs", true);
        xhttp.send(params);
    }

    function test(datasourceid, schemastr) {
        var params = "order=updateschema&datasourceid=" + datasourceid + "&schema=" + schemastr;
        alert("params: " + params);
    }
