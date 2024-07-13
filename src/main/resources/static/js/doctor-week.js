document.addEventListener('DOMContentLoaded', function() {
    let templateSelected = null;
    let weekSelected = null;

    let schedules = [];
    let templates = [];

    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        headerToolbar: {
            left: '',
            center: 'title',
            right: 'today prev,next'
        },
        editable: false,
        selectable: false,
        allDaySlot: false,
        firstDay: 1,
        slotMinTime: "07:00:00",
        slotMaxTime: "20:00:00",
        events: []
    });

    calendar.render();

    function formatDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Aggiunge lo zero se il mese è inferiore a 10
        const day = date.getDate().toString().padStart(2, '0'); // Aggiunge lo zero se il giorno è inferiore a 10
        return `${year}-${month}-${day}`;
    }

    function saveSchedule(){
        if (!templateSelected) {
            alert('Select a template first.');
            return;
        }


        let date = new Date(weekSelected);
        let schedule = {
            week: formatDate(date),
            slots: templateSelected.slots
        }

        console.log(formatDate(date), templateSelected.slots);

        fetch('/api/doctor/schedules', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(schedule)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to save schedule: ' + response.statusText);
                }
                return response.json();
            })
            .then(savedSchedule => {
                console.log('Schedule saved successfully:', JSON.stringify(savedSchedule));
                fetchUpdatedTemplates();
            })
            .then(() => {
                fetchUpdatedSchedules();
            })
            .catch(error => {
                console.error('Error saving schedule:', error);
            });

    }

    function alreadyScheduled() {
        let activeWeek = calendar.view.activeStart;

        // Converti activeWeek in LocalDate (solo data, senza ora)
        let activeLocalDate = new Date(activeWeek);
        // Sottraggo un giorno perchè non riesco a gestire il timezone
        // tra il client e il server
        activeLocalDate.setDate(activeLocalDate.getDate() - 1);

        activeLocalDate.setHours(0, 0, 0, 0); // Imposta ore, minuti, secondi e millisecondi a 0

        let alreadyScheduled = false;
        schedules.forEach(schedule => {
            // Converti schedule.week in LocalDate (solo data, senza ora)
            let scheduleLocalDate = new Date(schedule.week);
            scheduleLocalDate.setHours(0, 0, 0, 0); // Imposta ore, minuti, secondi e millisecondi a 0

            console.log(scheduleLocalDate, activeLocalDate);

            // Confronta solo il giorno (solo data, senza ora)
            if (scheduleLocalDate.getTime() === activeLocalDate.getTime()) {
                alreadyScheduled = true;
            }
        });

        return alreadyScheduled;
    }


    function onlyThisWeek(){
        if(alreadyScheduled()){
            alert('You have already scheduled this week');
            return;
        }
        saveSchedule();
    }
    document.getElementById('only-this-week').addEventListener('click', onlyThisWeek);

    function deleteSchedule() {
        // Ottieni la data della settimana attiva dal calendario
        let date = new Date(calendar.view.activeStart);

        console.log(formatDate(date));

        // Costruisci l'oggetto di schedule da inviare
        let schedule = {
            week: formatDate(date)
        };

        // Invia la richiesta DELETE utilizzando fetch
        fetch('/api/doctor/schedules', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
                // Includi eventuali altri header necessari, come token di autenticazione
            },
            body: JSON.stringify(schedule) // Invia l'oggetto schedule come corpo della richiesta
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete schedule: ' + response.statusText);
                }
                console.log('Schedule deleted successfully');
                clearSchedule();
                // Esegui eventuali azioni aggiuntive dopo la cancellazione, come aggiornare l'interfaccia utente
                fetchUpdatedSchedules(); // Esempio di funzione per aggiornare le schedule dopo la cancellazione
            })
            .catch(error => {
                console.error('Error deleting schedule:', error);
            });
    }
    document.getElementById('delete-this-week').addEventListener('click', deleteSchedule);

    function clearSchedule() {
        document.querySelectorAll('.template-item').forEach(item => item.classList.remove('selected'));
        renderEvents();
    }
    document.getElementById('clear-template').addEventListener('click', clearSchedule);


    // Funzione per caricare il template nel calendario
    function renderEvents(template = null) {

        // Mappa dei giorni della settimana a partire da lunedì
        const dayMap = {
            monday: 1,
            tuesday: 2,
            wednesday: 3,
            thursday: 4,
            friday: 5,
            saturday: 6,
            sunday: 7
        };

        const startOfWeek = calendar.view.activeStart;

        const events = []

        if(template!=null) {
            const eventsFromTemplate = eventFromTemplate(template, dayMap, startOfWeek);
            events.push(...eventsFromTemplate);
        }
        const eventsFromSchedule = eventFromSchedule(schedules, dayMap);

        events.push(...eventsFromSchedule);

        calendar.removeAllEvents();
        calendar.addEventSource(events);
    }

    function eventFromSchedule(schedules, dayMap) {
        const events = [];

        schedules.forEach(schedule => {
            for (const day in schedule.slots) {
                const dayIndex = dayMap[day];

                let startOfWeek = new Date(schedule.week);

                schedule.slots[day].forEach(slot => {
                    // Calcola la data esatta per ogni slot
                    let slotDate = new Date(startOfWeek);
                    slotDate.setDate(startOfWeek.getDate() + (dayIndex - startOfWeek.getDay()));

                    const start = new Date(slotDate);
                    const [startHour, startMinute] = slot.start.split(':');
                    start.setHours(startHour, startMinute);

                    const end = new Date(slotDate);
                    const [endHour, endMinute] = slot.end.split(':');
                    end.setHours(endHour, endMinute);

                    // Creazione dell'oggetto evento
                    const event = {
                        start: start.toISOString(),
                        end: end.toISOString(),
                    };

                    if(slot.taken){
                        event.backgroundColor = 'lightcoral';
                    }

                    events.push(event);
                });
            }
        });


        return events;
    }

    function eventFromTemplate(template, dayMap, startOfWeek) {
        const events = [];
        for (const day in template.slots) {
            const dayIndex = dayMap[day];

            template.slots[day].forEach(slot => {
                // Calcola la data esatta per ogni slot
                let slotDate = new Date(startOfWeek);
                slotDate.setDate(startOfWeek.getDate() + (dayIndex - startOfWeek.getDay()));

                const start = new Date(slotDate);
                const [startHour, startMinute] = slot.start.split(':');
                start.setHours(startHour, startMinute);

                const end = new Date(slotDate);
                const [endHour, endMinute] = slot.end.split(':');
                end.setHours(endHour, endMinute);

                const event = {
                    start: start.toISOString(),
                    end: end.toISOString(),
                    backgroundColor: 'lightblue',
                };
                events.push(event);
            });
        }
        return events;
    }


    function fetchUpdatedTemplates() {
        fetch('/api/doctor/templates')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch templates: ' + response.statusText);
                }
                return response.json();
            })
            .then(updatedTemplates => {
                templates = updatedTemplates;
                renderTemplates(updatedTemplates);
            })
            .catch(error => {
                console.error('Error fetching updated templates:', error);
            });
    }

    function fetchUpdatedSchedules() {
        fetch('/api/doctor/schedules')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch schedules: ' + response.statusText);
                }
                return response.json();
            })
            .then(updatedSchedules => {
                schedules = updatedSchedules;
                renderEvents(null);
            })
            .catch(error => {
                console.error('Error fetching updated schedules:', error);
            });
    }
    // Funzione per rendere i template nella lista
    function renderTemplates(templates) {
        const templateListEl = document.getElementById('template-list');
        templateListEl.innerHTML = ''; // Clear current list

        templates.forEach(template => {
            const div = document.createElement('div');
            div.classList.add('template-item');
            div.setAttribute('data-template-id', template.id);
            div.textContent = template.name;

            if (template.default) {
                div.classList.add('default-template');
            }

            div.addEventListener('click', function() {
                document.querySelectorAll('.template-item').forEach(item => item.classList.remove('selected'));
                templateSelected = template;
                weekSelected = calendar.view.activeStart;
                this.classList.add('selected');
                renderEvents(template);
            });

            templateListEl.appendChild(div);
        });
    }

    // Inizialmente carica i template dall'API
    fetchUpdatedTemplates();
    fetchUpdatedSchedules();


    // Imposta il template predefinito
    document.getElementById('set-default-template').addEventListener('click', function() {
        const selectedTemplate = document.querySelector('.template-item.selected');
        if (selectedTemplate) {
            const templateName = selectedTemplate.textContent;

            // Trova il template corrispondente nella lista globale di templates
            const template = templates.find(template => template.name === templateName);
            template.default = true;

            if (template) {
                // Esegui una richiesta PUT per impostare il template predefinito
                const putUrl = `/api/doctor/templates/default`;
                fetch(putUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                        // Aggiungi eventuali altri header necessari
                    },
                    body: JSON.stringify(template)
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Failed to set default template: ' + response.statusText);
                        }
                        fetchUpdatedTemplates();
                        clearSchedule();
                    })
                    .catch(error => {
                        console.error('Error setting default template:', error);
                    });
            } else {
                alert('Template not found in the global list.');
            }

        } else {
            alert('Select a template first.');
        }
    });

});
