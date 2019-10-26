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
        xhttp.open("POST", "https://pgcs-java.appspot.com/gcs", true);

        //Send the proper header information along with the request
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	//xhttp.setRequestHeader("Content-length", params.length);
	//xhttp.setRequestHeader("Connection", "close");

        xhttp.onreadystatechange = handler;
        xhttp.send(params);
} // end ajaxPostCall


function getSchema(datasourceid) {
        var params = "order=getschema&datasourceid=" + datasourceid;
        ajaxPostCall(params);
} // end getSchema


function updateSchema(datasourceid, schemastr) {
        var params = "order=updateschema&datasourceid=" + datasourceid + "&schema=" + schemastr;
        ajaxPostCall(params);
} // end updateSchema


function test(datasourceid, schemastr) {
        var params = "order=updateschema&datasourceid=" + datasourceid + "&schema=" + schemastr;
        console.log("PHS LOG: params: " + params);
} // end test
