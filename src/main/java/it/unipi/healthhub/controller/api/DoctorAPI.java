package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.dto.*;
import it.unipi.healthhub.model.*;
import it.unipi.healthhub.service.DoctorService;

import it.unipi.healthhub.util.ScheduleConverter;
import it.unipi.healthhub.util.TemplateConverter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorAPI {

    @Autowired
    private DoctorService doctorService;

    // Metodo per la ricerca dei medici
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> search(@RequestParam(name = "query", required = false) String query) {
        List<Doctor> doctors = doctorService.searchDoctors(query);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctor();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable String id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable String id, @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        if (updatedDoctor != null) {
            return ResponseEntity.ok(updatedDoctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for services
    @GetMapping("/{doctorId}/services")
    public ResponseEntity<List<Service>> getServices(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getServices(doctorId));
    }

    @DeleteMapping("/{doctorId}/services/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable String doctorId, @PathVariable Integer serviceId) {
        doctorService.deleteService(doctorId, serviceId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for appointments
    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getAppointments(doctorId));
    }

    @PostMapping("/{doctorId}/appointments")
    public ResponseEntity<Appointment> addAppointment(@PathVariable String doctorId, @RequestBody Appointment appointment) {
        return ResponseEntity.ok(doctorService.addAppointment(doctorId, appointment));
    }

    @DeleteMapping("/{doctorId}/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String doctorId, @PathVariable String appointmentId) {
        doctorService.deleteAppointment(doctorId, appointmentId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for templates
    @GetMapping("/{doctorId}/templates")
    public ResponseEntity<List<CalendarTemplate>> getTemplates(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getTemplates(doctorId));
    }

    @PostMapping("/{doctorId}/templates")
    public ResponseEntity<CalendarTemplate> addTemplate(@PathVariable String doctorId, @RequestBody CalendarTemplate template) {
        return ResponseEntity.ok(doctorService.addTemplate(doctorId, template));
    }



    @DeleteMapping("/{doctorId}/templates/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String doctorId, @PathVariable String templateId) {
        doctorService.deleteTemplate(doctorId, templateId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for calendars
    @GetMapping("/{doctorId}/calendars")
    public ResponseEntity<List<Schedule>> getCalendars(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getCalendars(doctorId));
    }

    @PostMapping("/{doctorId}/calendars")
    public ResponseEntity<Schedule> addCalendar(@PathVariable String doctorId, @RequestBody Schedule calendar) {
        return ResponseEntity.ok(doctorService.addCalendar(doctorId, calendar));
    }

    @PutMapping("/{doctorId}/calendars/{calendarId}")
    public ResponseEntity<Schedule> updateCalendar(@PathVariable String doctorId, @PathVariable Integer calendarId, @RequestBody Schedule calendar) {
        return ResponseEntity.ok(doctorService.updateCalendar(doctorId, calendarId, calendar));
    }

    // Endpoints for reviews
    @GetMapping("/{doctorId}/reviews")
    public ResponseEntity<List<Review>> getReviews(@PathVariable String doctorId) {
        List<Review> reviews = doctorService.getReviews(doctorId);
        if (reviews != null) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{doctorId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String doctorId, @RequestBody ReviewDTO review, HttpSession session) {
        // Set the current date and the name of the user
        LocalDate currentDate = LocalDate.now();
        review.setDate(currentDate);
        String name = (String) session.getAttribute("username");
        review.setName(name);

        Review newReview = doctorService.addReview(doctorId, review);
        if (newReview != null) {
            return ResponseEntity.ok(newReview);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{doctorId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String doctorId, @PathVariable Integer reviewId) {
        doctorService.deleteReview(doctorId, reviewId);
        return ResponseEntity.noContent().build();
    }

    // Endpoints for address

    @PutMapping("/address")
    public ResponseEntity<Address> updateMyAddress(@RequestBody Address address, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        return ResponseEntity.ok(doctorService.updateAddress(doctorId, address));
    }

    @PutMapping("/details")
    public ResponseEntity<UserDetailsDTO> updateMyDetails(@RequestBody UserDetailsDTO userDetails, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        return ResponseEntity.ok(doctorService.updateUserDetails(doctorId, userDetails));
    }

    @PostMapping("/phones")
    public ResponseEntity<PhoneNumberDTO> addMyNumber(@RequestBody PhoneNumberDTO request, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        String phoneNumber = request.getPhoneNumber();

        Integer newIndex = doctorService.addPhoneNumber(doctorId, phoneNumber);

        if (newIndex != null) {
            PhoneNumberDTO response = new PhoneNumberDTO(phoneNumber, newIndex);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/phones")
    public ResponseEntity<List<PhoneNumberDTO>> getMyNumbers(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<String> phoneNumbers = doctorService.getMyPhoneNumbers(doctorId);

        if (phoneNumbers != null) {
            List<PhoneNumberDTO> response = new ArrayList<>();
            for (int i = 0; i < phoneNumbers.size(); i++) {
                response.add(new PhoneNumberDTO(phoneNumbers.get(i), i));
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/phones/{index}")
    public ResponseEntity<String> removePhoneNumber(@PathVariable Integer index, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.removePhoneNumber(doctorId,index);
        if (removed) {
            return ResponseEntity.ok("Phone number removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/specializations")
    public ResponseEntity<SpecializationDTO> addMySpecialization(@RequestBody SpecializationDTO request, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        String specialization = request.getSpecialization();

        Integer newIndex = doctorService.addSpecialization(doctorId, specialization);

        if (newIndex != null) {
            SpecializationDTO response = new SpecializationDTO(specialization, newIndex);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationDTO>> getSpecializations(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<SpecializationDTO> specializations = doctorService.getSpecializations(doctorId);

        if (specializations != null) {
            return ResponseEntity.ok(specializations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/specializations/{index}")
    public ResponseEntity<String> removeSpecializations(@PathVariable Integer index, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.removeSpecialization(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Specialization removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceDTO> addVisitType(@RequestBody ServiceDTO serviceDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");

        it.unipi.healthhub.model.Service service = new it.unipi.healthhub.model.Service();
        service.setService(serviceDto.getService());
        service.setPrice(serviceDto.getPrice());

        Integer newIndex = doctorService.addService(doctorId, service);

        if (newIndex != null) {
            ServiceDTO responseDto = new ServiceDTO();
            responseDto.setIndex(newIndex);
            responseDto.setService(serviceDto.getService());
            responseDto.setPrice(serviceDto.getPrice());

            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceDTO>> getMyServices(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<it.unipi.healthhub.model.Service> services = doctorService.getMyServices(doctorId);

        if (services != null) {
            List<ServiceDTO> response = new ArrayList<>();
            for (int i = 0; i < services.size(); i++) {
                ServiceDTO serviceDto = new ServiceDTO();
                serviceDto.setIndex(i);
                serviceDto.setService(services.get(i).getService());
                serviceDto.setPrice(services.get(i).getPrice());
                response.add(serviceDto);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PutMapping("/services/{index}")
    public ResponseEntity<String> updateVisitType(@PathVariable Integer index, @RequestBody ServiceDTO serviceDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");

        it.unipi.healthhub.model.Service service = new it.unipi.healthhub.model.Service();
        service.setService(serviceDto.getService());
        service.setPrice(serviceDto.getPrice());

        boolean updated = doctorService.updateService(doctorId, index, service);
        if (updated) {
            return ResponseEntity.ok("Service updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/services/{index}")
    public ResponseEntity<String> removeService(@PathVariable Integer index, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteService(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Service removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/templates")
    public ResponseEntity<TemplateDTO> addTemplate(@RequestBody TemplateDTO templateDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");

        CalendarTemplate template = new CalendarTemplate();
        template.setName(templateDto.getName());

        Map<String, List<TemplateDTO.SlotDTO>> dtoSlots = templateDto.getSlots();
        Map<String, List<Slot>> modelSlots = TemplateConverter.convertToModelSlots(dtoSlots);
        template.setSlots(modelSlots);

        CalendarTemplate newTemplate = doctorService.addTemplate(doctorId, template);

        if (newTemplate != null) {
            modelSlots = newTemplate.getSlots();
            dtoSlots = TemplateConverter.convertToDtoSlots(modelSlots);

            TemplateDTO response = new TemplateDTO(newTemplate.getId(), newTemplate.getName(), dtoSlots);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/templates/{templateId}")
    public ResponseEntity<String> updateTemplate(@PathVariable String templateId, @RequestBody TemplateDTO templateDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");

        CalendarTemplate template = new CalendarTemplate();
        template.setName(templateDto.getName());

        Map<String, List<TemplateDTO.SlotDTO>> dtoSlots = templateDto.getSlots();
        Map<String, List<Slot>> modelSlots = TemplateConverter.convertToModelSlots(dtoSlots);

        template.setSlots(modelSlots);
        template.setId(templateId);

        CalendarTemplate updated = doctorService.updateTemplate(doctorId, template);
        if (updated != null) {
            return ResponseEntity.ok("Template updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/templates/default")
    public ResponseEntity<String> setDefaultTemplate(@RequestBody TemplateDTO templateDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        String templateId = templateDto.getId();
        boolean updated = doctorService.setDefaultTemplate(doctorId, templateId);
        if (updated) {
            return ResponseEntity.ok("Default template updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<List<TemplateDTO>> getMyTemplates(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<CalendarTemplate> templates = doctorService.getMyTemplates(doctorId);

        if (templates != null) {
            List<TemplateDTO> response = new ArrayList<>();
            for (CalendarTemplate template : templates) {
                Map<String, List<Slot>> modelSlots = template.getSlots();
                Map<String, List<TemplateDTO.SlotDTO>> dtoSlots = TemplateConverter.convertToDtoSlots(modelSlots);

                TemplateDTO templateDto = new TemplateDTO(template.getId(), template.getName(), dtoSlots, template.isActive());
                response.add(templateDto);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<String> removeTemplate(@PathVariable String templateId, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteTemplate(doctorId, templateId);
        if (removed) {
            return ResponseEntity.ok("Template removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody ScheduleDTO scheduleDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");

        Schedule schedule = new Schedule();
        schedule.setWeek(scheduleDto.getWeek());

        Map<String, List<ScheduleDTO.PrenotableSlotDTO>> dtoSlots = scheduleDto.getSlots();
        Map<String, List<PrenotableSlot>> modelSlots = ScheduleConverter.convertToModelSlots(dtoSlots);
        schedule.setSlots(modelSlots);

        Schedule newSchedule = doctorService.addCalendar(doctorId, schedule);

        if (newSchedule != null) {
            modelSlots = newSchedule.getSlots();
            dtoSlots = ScheduleConverter.convertToDtoSlots(modelSlots);

            ScheduleDTO response = new ScheduleDTO(newSchedule.getWeek(), dtoSlots);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDTO>> getMySchedules(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<Schedule> schedules = doctorService.getCalendars(doctorId);

        if (schedules != null) {
            List<ScheduleDTO> response = new ArrayList<>();
            for (Schedule schedule : schedules) {
                Map<String, List<PrenotableSlot>> modelSlots = schedule.getSlots();
                Map<String, List<ScheduleDTO.PrenotableSlotDTO>> dtoSlots = ScheduleConverter.convertToDtoSlots(modelSlots);

                ScheduleDTO scheduleDto = new ScheduleDTO(schedule.getWeek(), dtoSlots);
                response.add(scheduleDto);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/schedules")
    public ResponseEntity<String> removeSchedule(@RequestBody ScheduleDTO scheduleDto, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteCalendar(doctorId, scheduleDto.getWeek());
        if (removed) {
            return ResponseEntity.ok("Schedule removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getMyReviews(HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        List<Review> reviews = doctorService.getReviews(doctorId);
        if (reviews != null) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/reviews/{index}")
    public ResponseEntity<String> removeReview(@PathVariable Integer index, HttpSession session) {
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteReview(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Review removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
