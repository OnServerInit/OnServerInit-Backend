function changeUpdateStatus(clicked){
    $.ajax({
        url: 'http://localhost:8080/resources/' + document.getElementById("id").value + '/update/'
            + clicked.name + '/status',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            status: "archived"
        },
        success: function(data){
            clicked.parentElement.parentElement.parentElement.remove();
        }
    });
}