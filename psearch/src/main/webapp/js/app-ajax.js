$(document).ready(function() {
        $('#result').blur(function(event) {
                // var + variable for the result, and name of field to send???
                var result = $('#result').val();
                // get + Name of Servlet
                $.get('GCSController', {
                        result : result
                }, function(responseText) {
                        // ajaxGetUserServletResponse is the id of the div where you will present results
                        $('#ajaxGetUserServletResponse').text(responseText);
                });
        });
});