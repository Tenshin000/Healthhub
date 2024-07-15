package it.unipi.healthhub.util;

import it.unipi.healthhub.dto.ScheduleDTO;
import it.unipi.healthhub.dao.mongo.PrenotableSlot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleConverter {

    // Convert DTO slots to model slots
    public static Map<String, List<PrenotableSlot>> convertToModelSlots(Map<String, List<ScheduleDTO.PrenotableSlotDTO>> dtoSlots) {
        return dtoSlots.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(dtoSlot -> new PrenotableSlot(
                                        dtoSlot.getStart(),
                                        dtoSlot.getEnd(),
                                        dtoSlot.isTaken()
                                ))
                                .collect(Collectors.toList())
                ));
    }

    // Convert model slots to DTO slots
    public static Map<String, List<ScheduleDTO.PrenotableSlotDTO>> convertToDtoSlots(Map<String, List<PrenotableSlot>> modelSlots) {
        return modelSlots.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(modelSlot -> new ScheduleDTO.PrenotableSlotDTO(
                                        modelSlot.getStart(),
                                        modelSlot.getEnd(),
                                        modelSlot.isTaken()
                                ))
                                .collect(Collectors.toList())
                ));
    }
}
