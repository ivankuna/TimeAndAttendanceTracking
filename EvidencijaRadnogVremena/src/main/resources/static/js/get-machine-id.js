let machineId = localStorage.getItem('MachineId');
    if (!machineId) {
        machineId = "Nema postavljenog ID-a";
    } else {
        machineId = "Machine ID: " + machineId;
    }
    document.getElementById('machineIdSpan').innerText = machineId;