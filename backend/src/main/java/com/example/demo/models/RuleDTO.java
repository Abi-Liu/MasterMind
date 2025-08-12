package com.example.demo.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RuleDTO {
    @Min(4) @Max(10)
    private int codeLength;
//    private boolean allowDuplicates;
    @Min(1) @Max(9)
    private int maxDigit;
    @Min(1) @Max(20)
    private int maxAttempts;

    @Min(0)
    private int maxHints;

    // overloaded constructor to avoid breaking tests
    public RuleDTO(int codeLength, int maxDigit, int maxAttempts) {
        this(codeLength, maxDigit, maxAttempts, 1);
    }
}
