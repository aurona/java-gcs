    <!-- Scripts to execute actions -->

    function getSchema() {
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                document.getElementById("res").innerHTML =
                this.responseText;
                }
        };
        xhttp.open("GET", "https://java-gcs-256106.appspot.com/hello?order=getschema", true);
        xhttp.send();
    }

    function updateSchema(datasourceid) {
        data: document.getElementsByTagName("textarea") [0].value
        alert("id: " + datasourceid);

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                document.getElementById("res").innerHTML =
                this.responseText;
                }
        };
        xhttp.open("GET", "https://java-gcs-256106.appspot.com/hello?order=updateschema", true);
        xhttp.send();
    }


    function test(datasourceid) {
        alert("dsid: " + datasourceid);
    }
