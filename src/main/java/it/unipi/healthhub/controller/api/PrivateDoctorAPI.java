package it.unipi.healthhub.controller.api;

import it.unipi.healthhub.exception.ScheduleAlreadyExistsException;
import it.unipi.healthhub.model.mongo.*;
import it.unipi.healthhub.dto.*;
import it.unipi.healthhub.service.AppointmentService;
import it.unipi.healthhub.service.DoctorService;
import it.unipi.healthhub.util.HashUtil;
import it.unipi.healthhub.util.ScheduleConverter;
import it.unipi.healthhub.util.TemplateConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class PrivateDoctorAPI {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getMyAppointments(
            HttpSession session,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (date == null) {
            date = LocalDate.now();
        }
        List<Appointment> appointments = appointmentService.getAppointmentsForDay(doctorId, date);
        if (appointments != null) {
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String appointmentId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean deleted = doctorService.cancelAnAppointment(doctorId, appointmentId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/address")
    public ResponseEntity<Address> updateMyAddress(@RequestBody Address address, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        return ResponseEntity.ok(doctorService.updateAddress(doctorId, address));
    }

    @PutMapping("/details")
    public ResponseEntity<DoctorDetailsDTO> updateMyDetails(@RequestBody DoctorDetailsDTO doctorDetails, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        return ResponseEntity.ok(doctorService.updateDoctorDetails(doctorId, doctorDetails));
    }

    @PostMapping("/phones")
    public ResponseEntity<PhoneNumberDTO> addMyNumber(@RequestBody PhoneNumberDTO phoneRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        String phoneNumber = phoneRequest.getPhoneNumber();

        Integer newIndex = doctorService.addPhoneNumber(doctorId, phoneNumber);

        if (newIndex != null) {
            PhoneNumberDTO response = new PhoneNumberDTO(phoneNumber, newIndex);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/phones")
    public ResponseEntity<List<PhoneNumberDTO>> getMyNumbers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
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
    public ResponseEntity<String> removePhoneNumber(@PathVariable Integer index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.removePhoneNumber(doctorId,index);
        if (removed) {
            return ResponseEntity.ok("Phone number removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/specializations")
    public ResponseEntity<SpecializationDTO> addMySpecialization(@RequestBody SpecializationDTO specRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        String specialization = specRequest.getSpecialization();

        Integer newIndex = doctorService.addSpecialization(doctorId, specialization);

        if (newIndex != null) {
            SpecializationDTO response = new SpecializationDTO(specialization, newIndex);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationDTO>> getSpecializations(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        List<SpecializationDTO> specializations = doctorService.getSpecializations(doctorId);

        if (specializations != null) {
            return ResponseEntity.ok(specializations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/specializations/{index}")
    public ResponseEntity<String> removeSpecializations(@PathVariable Integer index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.removeSpecialization(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Specialization removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceDTO> addVisitType(@RequestBody ServiceDTO serviceDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");

        Service service = new Service();
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
    public ResponseEntity<List<ServiceDTO>> getMyServices(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        List<Service> services = doctorService.getMyServices(doctorId);

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
    public ResponseEntity<String> updateVisitType(@PathVariable Integer index, @RequestBody ServiceDTO serviceDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");

        Service service = new Service();
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
    public ResponseEntity<String> removeService(@PathVariable Integer index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteService(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Service removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/templates")
    public ResponseEntity<TemplateDTO> addTemplate(@RequestBody TemplateDTO templateDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
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
    public ResponseEntity<String> updateTemplate(@PathVariable String templateId, @RequestBody TemplateDTO templateDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
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
    public ResponseEntity<String> setDefaultTemplate(@RequestBody TemplateDTO templateDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
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
    public ResponseEntity<List<TemplateDTO>> getMyTemplates(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        List<CalendarTemplate> templates = doctorService.getMyTemplates(doctorId);

        if (templates != null) {
            List<TemplateDTO> response = new ArrayList<>();
            for (CalendarTemplate template : templates) {
                Map<String, List<Slot>> modelSlots = template.getSlots();
                Map<String, List<TemplateDTO.SlotDTO>> dtoSlots = TemplateConverter.convertToDtoSlots(modelSlots);

                TemplateDTO templateDto = new TemplateDTO(template.getId(), template.getName(), dtoSlots, template.isDefault());
                response.add(templateDto);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<String> removeTemplate(@PathVariable String templateId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteTemplate(doctorId, templateId);
        if (removed) {
            return ResponseEntity.ok("Template removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleDTO> addSchedule(@RequestBody ScheduleDTO scheduleDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");

        Schedule schedule = new Schedule();
        schedule.setWeek(scheduleDto.getWeek());

        Map<String, List<ScheduleDTO.PrenotableSlotDTO>> dtoSlots = scheduleDto.getSlots();
        Map<String, List<PrenotableSlot>> modelSlots = ScheduleConverter.convertToModelSlots(dtoSlots);
        schedule.setSlots(modelSlots);

        try{
            Schedule newSchedule = doctorService.addSchedule(doctorId, schedule);
            if (newSchedule != null) {
                modelSlots = newSchedule.getSlots();
                dtoSlots = ScheduleConverter.convertToDtoSlots(modelSlots);

                ScheduleDTO response = new ScheduleDTO(newSchedule.getWeek(), dtoSlots);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        catch(ScheduleAlreadyExistsException saee){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDTO>> getMySchedules(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        List<Schedule> schedules = doctorService.getSchedules(doctorId);

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
    public ResponseEntity<String> removeSchedule(@RequestBody ScheduleDTO scheduleDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteCalendar(doctorId, scheduleDto.getWeek());
        if (removed) {
            return ResponseEntity.ok("Schedule removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getMyReviews(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        List<Review> reviews = doctorService.getReviews(doctorId);
        if (reviews != null) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/reviews/{index}")
    public ResponseEntity<String> removeReview(@PathVariable Integer index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        boolean removed = doctorService.deleteReview(doctorId, index);
        if (removed) {
            return ResponseEntity.ok("Review removed successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request, @RequestBody PasswordChangeDTO passwords) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        passwords.setCurrentPassword(HashUtil.hashPassword(passwords.getCurrentPassword()));
        passwords.setNewPassword(HashUtil.hashPassword(passwords.getNewPassword()));

        boolean ok = doctorService.changePassword(doctorId, passwords.getCurrentPassword(), passwords.getNewPassword());
        if (ok) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/analytics/visits")
    public ResponseEntity<Map<String,Integer>> getVisitsAnalytics(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        Map<String,Integer> visitData = doctorService.getVisitsAnalytics(doctorId);
        if (visitData != null) {
            return ResponseEntity.ok(visitData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/analytics/earnings")
    public ResponseEntity<Map<String,Double>> getEarningsAnalytics(HttpServletRequest request, @RequestParam(value = "year", required = false) Integer year) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        Map<String,Double> earningsData = doctorService.getEarningsAnalytics(doctorId, year);
        if (earningsData != null) {
            return ResponseEntity.ok(earningsData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/analytics/visits/distribution")
    public ResponseEntity<Map<String,Integer>> getVisitsAnalyticsWeek(HttpServletRequest request, @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "week", required = false) Integer week) {
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");

        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (week == null) {
            week = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        }

        Map<String,Integer> visitData = doctorService.getVisitsAnalyticsWeek(doctorId, week, year);
        if (visitData != null) {
            return ResponseEntity.ok(visitData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/analytics/patients")
    public ResponseEntity<Integer> getNewPatientsAnalytics(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String doctorId = (String) session.getAttribute("doctorId");

        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        Integer count = doctorService.getAnalyticsNewPatientsByMonth(doctorId, year, month);

        return ResponseEntity.ok(count);
    }
}
