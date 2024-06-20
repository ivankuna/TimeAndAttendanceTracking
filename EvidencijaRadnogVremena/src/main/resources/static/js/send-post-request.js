       document.getElementById('oib').addEventListener('blur', function(event) {

        var oibValue = document.getElementById('oib').value;

        sendPostRequest(oibValue);
    });

    function sendPostRequest(oib) {
        fetch('/employees/check-oib', {
         method: 'POST',
         headers: {
             'Content-Type': 'application/json'
         },
         body: JSON.stringify({ oib: oib })
        }).then(response => {

         if (response.ok) {

             return response.text();
         } else {
             console.error('Greška prilikom slanja POST zahtjeva:', response.statusText);
             return null;
         }
        }).then(url => {
         if (url) {

             if (url != '') {
             window.location = url;
             }
            } else {

             console.error('Nije dobiven URL kao odgovor na POST zahtjev.');
            }
        }).catch(error => {
        console.error('Greška prilikom slanja POST zahtjeva:', error);
    });
}