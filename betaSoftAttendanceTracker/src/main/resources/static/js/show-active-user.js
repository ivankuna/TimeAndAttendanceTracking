    $(document).ready(function() {
        const fixedMessage = '\nTehnička podrška:\n\n+385 (91) 2275 504\n+385 (98) 275 504';

        $('#logoutLink').click(function(e) {
            e.preventDefault();
            alert(fixedMessage);
        });
    });