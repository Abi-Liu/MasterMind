package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/* Player can set the rules prior to creating a new game
   They are able to customize the code length, whether duplicates are allowed, and maximum number of
   tries to correctly guess the code
 */
public class Rules {
    private int codeLength;

//    private boolean allowDuplicates;

    // highest digit.
    // digits in the code will range from 0 - maxDigit (inclusive)
    private int maxDigit;

    private int maxAttempts;
}
