class DoctorScheduleCalendar {
    constructor(calendarId, doctorId) {
        this.calendar = document.getElementById(calendarId);
        this.prevBtn = this.calendar.querySelector('#prevBtn');
        this.nextBtn = this.calendar.querySelector('#nextBtn');
        this.weekInfo = this.calendar.querySelector('#week-info');
        this.daysContainer = this.calendar.querySelector('#days');
        this.doctorId = doctorId;
        this.currentDate = new Date();

        this.init();
    }

    init() {
        this.prevBtn.addEventListener('click', this.prevWeek.bind(this));
        this.nextBtn.addEventListener('click', this.nextWeek.bind(this));
        this.updateWeekInfo();
    }

    formatDate(date) {
        const options = { month: 'short', day: 'numeric' };
        return date.toLocaleDateString('en-US', options);
    }

    getWeekRange(date) {
        const startOfWeek = new Date(date);
        startOfWeek.setDate(date.getDate() - date.getDay() + 1); // Inizia da lunedì
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // Fine domenica

        const startMonthDay = `${startOfWeek.toLocaleString('en-US', { month: 'short' })} ${startOfWeek.getDate()}`;
        const endDay = endOfWeek.getDate();
        const year = endOfWeek.getFullYear();

        return `${startMonthDay}-${endDay}, ${year}`;
    }

    fetchScheduleData(year, week) {
        const url = `/api/doctors/${this.doctorId}/schedules/week?year=${year}&week=${week}`;
        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error fetching schedule data: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .catch(error => {
                console.error(error);
                // Puoi gestire ulteriormente l'errore qui o rilanciarlo per gestirlo altrove
            });
    }

    updateWeekInfo() {
        console.log(this.getWeekNumber(this.currentDate));
        const year = this.currentDate.getFullYear();
        const week = this.getWeekNumber(this.currentDate);
        console.log(year, week)
        console.log(this.currentDate);

        this.weekInfo.textContent = this.getWeekRange(this.currentDate);

        this.fetchScheduleData(year, week).then(data => {
            this.updateDays(data);
        });
    }

    updateDays(scheduleData = null) {
        const startOfWeek = new Date(this.currentDate);
        startOfWeek.setDate(this.currentDate.getDate() - this.currentDate.getDay() + 1);
        console.log(startOfWeek);

        this.daysContainer.innerHTML = '';

        const dayNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'];
        const keyDays = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday'];
        for (let i = 0; i < 5; i++) {
            const day = new Date(startOfWeek);
            day.setDate(startOfWeek.getDate() + i);

            const dayDiv = document.createElement('div');
            dayDiv.classList.add('day');

            // workaround per il formato della data
            let year = day.getFullYear();
            let month = (day.getMonth() + 1).toString().padStart(2, '0');
            let dayOfMonth = day.getDate().toString().padStart(2, '0');
            dayDiv.setAttribute('data-date', `${year}-${month}-${dayOfMonth}`);

            const dateLabel = document.createElement('div');
            dateLabel.classList.add('datelabel');
            dateLabel.innerHTML = `
                <div class="day-of-week"><strong>${dayNames[i]}</strong></div>
                <div class="day-and-month">${this.formatDate(day)}</div>
            `;

            dayDiv.appendChild(dateLabel);

            if (scheduleData && scheduleData.slots && scheduleData.slots[keyDays[i]]) {
                const timeSlots = scheduleData.slots[keyDays[i]];
                timeSlots.forEach(slot => {
                    const timeSlotDiv = document.createElement('div');
                    timeSlotDiv.classList.add('timeslot');
                    timeSlotDiv.setAttribute('data-slot', slot.start);

                    if (slot.taken) {
                        timeSlotDiv.classList.add('booked');
                    } else {
                        timeSlotDiv.addEventListener('click', this.slotClickHandler.bind(this));
                    }

                    timeSlotDiv.textContent = slot.start;
                    dayDiv.appendChild(timeSlotDiv);
                });
            }

            this.daysContainer.appendChild(dayDiv);
        }
    }

    slotClickHandler(event) {
        const target = event.target;
        if (target.classList.contains('timeslot')) {
            const selectedSlots = this.daysContainer.querySelectorAll('.timeslot.selected');
            selectedSlots.forEach(slot => slot.classList.remove('selected'));
            target.classList.add('selected');
        }
    }

    prevWeek() {
        this.currentDate.setDate(this.currentDate.getDate() - 7);
        this.updateWeekInfo();
    }

    nextWeek() {
        this.currentDate.setDate(this.currentDate.getDate() + 7);
        this.updateWeekInfo();
    }

    getWeekNumber(date) {
        const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
        const millisecondsInDay = 86400000; // 1000 * 60 * 60 * 24
        const startOfWeek = firstDayOfYear.getTime() - (firstDayOfYear.getDay() * millisecondsInDay);
        const dayOfYear = (date.getTime() - startOfWeek) / millisecondsInDay;
        return Math.ceil((dayOfYear + firstDayOfYear.getDay()) / 7)-1;
    }

    getSelectedSlot() {
        const selectedSlot = this.daysContainer.querySelector('.timeslot.selected');
        if (selectedSlot) {
            const dayDiv = selectedSlot.closest('.day');
            return {
                date: dayDiv.getAttribute('data-date'),
                slot: selectedSlot.getAttribute('data-slot')
            };
        }
        return null;
    }

}