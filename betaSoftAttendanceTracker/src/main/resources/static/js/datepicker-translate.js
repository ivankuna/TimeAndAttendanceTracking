jQuery(function($) {
        $.datepicker.regional['hr'] = {
            clearText: 'izbriši',
            clearStatus: 'Izbriši trenutni datum',
            closeText: 'Zatvori',
            closeStatus: 'Zatvori kalendar',
            prevText: '&#x3c;&#x3c;',
            prevStatus: 'Prikaži prethodni mjesec',
            nextText: '&#x3e;&#x3e;',
            nextStatus: 'Prikaži slijedeći mjesec',
            currentText: 'Danas',
            currentStatus: 'Današnji datum',
            monthNames: ['Siječanj', 'Veljača', 'Ožujak', 'Travanj', 'Svibanj', 'Lipanj', 'Srpanj', 'Kolovoz', 'Rujan', 'Listopad', 'Studeni', 'Prosinac'],
            monthNamesShort: ['Sij', 'Velj', 'Ožu', 'Tra', 'Svi', 'Lip', 'Srp', 'Kol', 'Ruj', 'Lis', 'Stu', 'Pro'],
            monthStatus: 'Prikaži mjesece',
            yearStatus: 'Prikaži godine',
            weekHeader: 'Tje',
            weekStatus: 'Tjedan',
            dayNames: ['Nedjelja', 'Ponedjeljak', 'Utorak', 'Srijeda', 'Četvrtak', 'Petak', 'Subota'],
            dayNamesShort: ['Ned', 'Pon', 'Uto', 'Sri', 'Čet', 'Pet', 'Sub'],
            dayNamesMin: ['Ne', 'Po', 'Ut', 'Sr', 'Če', 'Pe', 'Su'],
            dayStatus: 'Odaber DD za prvi dan tjedna',
            dateStatus: 'Datum D, M d',
            dateFormat: 'dd.mm.yy.',
            firstDay: 1,
            initStatus: 'Odaberi datum',
        };
            $.datepicker.setDefaults($.datepicker.regional['hr']);
        });