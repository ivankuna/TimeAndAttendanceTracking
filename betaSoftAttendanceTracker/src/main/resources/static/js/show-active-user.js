    $(document).ready(function(){
        $('#logoutLink').click(function(e){
        e.preventDefault();
        var username = $('#usernameMessage span').text();
        alert(username);
        });
    });