var input = document.getElementById("schemaFile");
var output = document.getElementById("fileread");

input.addEventListener("change", function () {
        if (this.files && this.files[0]) {
                var schemaFile = this.files[0];
                var reader = new FileReader();
                
                reader.addEventListener('load', function (e) {
                output.textContent = e.target.result;
                });
                
                reader.readAsText(schemaFile);
        }   
});         


function test(datasourceid, schemajson, sch) {
        var formData = new FormData();
        formData.append("order", "test");
        
        formData.append("datasourceid", datasourceid);
        console.log("PHS LOG: test datasourceid: " + datasourceid);
        
        formData.append("schemajson", schemajson);
        console.log("PHS LOG: test schemajson: " + schemajson);
        
        console.log("PHS LOG: test schemafile 0: " + sch);
        formData.append("schemafile", document.getElementById('fileread').value);
        console.log("PHS LOG: test schemafile 1: " + document.getElementById('fileread').value);
        sch = JSON.stringify(document.getElementById('fileread').value);
        console.log("PHS LOG: test schemafile 2: " + sch);
        
        ajaxPostCallFormData(formData);
} // end test
