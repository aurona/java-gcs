// Global XMLHttpRequest for all methods
var xhttp = getHTTPObject();


// Scripts to execute actions
function getHTTPObject() {
        console.log("PHS LOG: getHTTPObject start");

        var xhttp = false;
	// Use IE's ActiveX items to load the file.
	if(typeof ActiveXObject != 'undefined') {
		try {xhttp = new ActiveXObject("Msxml2.XMLHTTP");}
		catch (e) {
			try {xhttp = new ActiveXObject("Microsoft.XMLHTTP");}
			catch (E) {xhttp = false;}
		}
	//If ActiveX is not available, use the XMLHttpRequest of Firefox/Mozilla etc. to load the document.
	} else if (XMLHttpRequest) {
		try { xhttp = new XMLHttpRequest(); }
		catch (e) {xhttp = false;}
        }

	return xhttp;
} // end getHTTPObject


function handler() { // Call a function when the state changes.
        console.log("PHS LOG: handler start");
        if(xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("PHS LOG: handler inside if: " + xhttp.responseText);
                //$("res").value = xhttp.responseText;
                document.getElementById('res').innerHTML = xhttp.responseText;
        }

} // end handler


function ajaxPostCall(params) {
        console.log("PHS LOG: ajaxPostCall with params: " + params);
        xhttp.open("POST", "/gcs", true); // Name of Servlet, without https://pgcs-java.appspot.com/gcs

        // Send the proper header information along with the request. Required for POST method
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");  // If you use URL encoded parameters to sent the parameters

        xhttp.onreadystatechange = handler;
        xhttp.send(params);
} // end ajaxPostCall

function ajaxPostCallFormData(fd) {
        xhttp.open("POST", "/gcs", true); // Name of Servlet, without https://pgcs-java.appspot.com/gcs
	xhttp.setRequestHeader("Content-type", "multipart/form-data"); // If form-data, Servlet needs directive @MultipartConfig
        xhttp.onreadystatechange = handler;
        xhttp.send(fd);
} // end ajaxPostCall


function getSchema(datasourceid) {
        var params = "order=getschema&datasourceid=" + datasourceid;
        ajaxPostCall(params);
} // end getSchema


function updateSchemaJSON(datasourceid, schemastr) {
        var params = "order=updateschemajson&datasourceid=" + datasourceid + "&schema=" + encodeURI(schemastr);
        console.log("PHS LOG: updateSchemaJSON params: " + params);
        ajaxPostCall(params);
} // end updateSchemaJSON


function updateSchemaFile(datasourceid, schemastr) {
        console.log("PHS LOG: updateSchemaFile datasourceid: " + datasourceid);
        var formData = new FormData();
        formData.append('order', 'updateschemafile');
        formData.append('datasourceid', datasourceid);
        formData.append('schema', schemastr);

        console.log("PHS LOG: updateSchemaFile params added");

        // Display the values
        for (var value of formData.values())
                console.log(value);

        xhttp.open("POST", "/gcs", true);
	xhttp.setRequestHeader("Content-type", "multipart/form-data"); // If form-data, Servlet needs directive @MultipartConfig
        xhttp.onreadystatechange = handler;
        xhttp.send(formData);
} // end updateSchemaFile


function test(datasourceid, schemastr) {
        var formData = new FormData();
        formData.append('order', 'test');
        formData.append('datasourceid', datasourceid);
        formData.append('schema', schemastr);
        ajaxPostCallFormData(formData);
} // end test
