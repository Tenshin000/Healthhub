package it.unipi.healthhub.util;

import it.unipi.healthhub.model.mongo.Slot;
import it.unipi.healthhub.dto.TemplateDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateConverter {

    public static Map<String, List<Slot>> convertToModelSlots(Map<String, List<TemplateDTO.SlotDTO>> dtoSlots) {
        return dtoSlots.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(dtoSlot -> new Slot(dtoSlot.getStart(), dtoSlot.getEnd()))
                                .collect(Collectors.toList())
                ));
    }

    public static Map<String, List<TemplateDTO.SlotDTO>> convertToDtoSlots(Map<String, List<Slot>> modelSlots) {
        return modelSlots.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(modelSlot -> new TemplateDTO.SlotDTO(modelSlot.getStart(), modelSlot.getEnd()))
                                .collect(Collectors.toList())
                ));
    }
}
