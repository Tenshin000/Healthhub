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
        const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Adds leading zero if month is less than 10
        const day = date.getDate().toString().padStart(2, '0'); // Adds leading zero if day is less than 10
        return `${year}-${month}-${day}`;
    }

    function saveSchedule() {
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
                if (response.status === 409) {
                    alert('You have already scheduled this week');
                    throw new Error('Conflict: Schedule already exists for this week.');
                }
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

        // Convert activeWeek to LocalDate (date only, no time)
        let activeLocalDate = new Date(activeWeek);

        activeLocalDate.setHours(0, 0, 0, 0); // Set hours, minutes, seconds, and milliseconds to 0

        let alreadyScheduled = false;
        schedules.forEach(schedule => {
            // Convert schedule.week to LocalDate (date only, no time)
            let scheduleLocalDate = new Date(schedule.week);
            scheduleLocalDate.setHours(0, 0, 0, 0); // Set hours, minutes, seconds, and milliseconds to 0

            console.log(scheduleLocalDate, activeLocalDate);

            // Compare only the day (date only, no time)
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
        // Get the active week date from the calendar
        let date = new Date(calendar.view.activeStart);

        console.log(formatDate(date));

        // Build the schedule object to send
        let schedule = {
            week: formatDate(date)
        };

        // Send DELETE request using fetch
        fetch('/api/doctor/schedules', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
                // Include any other necessary headers, such as authentication token
            },
            body: JSON.stringify(schedule) // Send the schedule object as the request body
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete schedule: ' + response.statusText);
                }
                console.log('Schedule deleted successfully');
                clearSchedule();
                // Perform any additional actions after deletion, like updating the UI
                fetchUpdatedSchedules(); // Example function to update schedules after deletion
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


    // Function to load the template into the calendar
    function renderEvents(template = null) {

        // Map of weekdays starting from Monday
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
                    // Calculate the exact date for each slot
                    let slotDate = new Date(startOfWeek);
                    slotDate.setDate(startOfWeek.getDate() + (dayIndex - startOfWeek.getDay()));

                    const start = new Date(slotDate);
                    const [startHour, startMinute] = slot.start.split(':');
                    start.setHours(startHour, startMinute);

                    const end = new Date(slotDate);
                    const [endHour, endMinute] = slot.end.split(':');
                    end.setHours(endHour, endMinute);

                    // Create the event object
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
                // Calculate the exact date for each slot
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

    // Function to render templates in the list
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

    // Initially load templates from the API
    fetchUpdatedTemplates();
    fetchUpdatedSchedules();

    // Set the default template
    document.getElementById('set-default-template').addEventListener('click', function() {
        const selectedTemplate = document.querySelector('.template-item.selected');
        if (selectedTemplate) {
            const templateName = selectedTemplate.textContent;

            // Find the corresponding template in the global templates list
            const template = templates.find(template => template.name === templateName);
            template.default = true;

            if (template) {
                // Perform a PUT request to set the default template
                const putUrl = `/api/doctor/templates/default`;
                fetch(putUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                        // Add any other necessary headers
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
