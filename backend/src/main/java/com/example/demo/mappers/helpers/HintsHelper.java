package com.example.demo.mappers.helpers;

import com.example.demo.models.HintResponseDTO;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HintsHelper {
    // helper method to convert the LinkedHashMap to a List that will be returned to client
    @Named("hintsToList")
    public List<HintResponseDTO> hintsToList(Map<Integer, Integer> hints){
        if(hints == null || hints.isEmpty()) {
            return List.of();
        }

        List<HintResponseDTO> res = new ArrayList<>(hints.size());
        for(Map.Entry<Integer, Integer> entry : hints.entrySet()) {
            res.add(new HintResponseDTO(entry.getKey(), entry.getValue()));
        }

        return res;
    }
}
