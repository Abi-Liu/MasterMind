package com.example.demo.entities;

import lombok.Data;

@Data
/* Player can set the rules prior to creating a new game
   They are able to customize the code length, whether duplicates are allowed, and maximum number of
   tries to correctly guess the code
 */
public class Rules {
    private int codeLength;
    
    private boolean allowDuplicates;

    private int maxAttempts;
}
