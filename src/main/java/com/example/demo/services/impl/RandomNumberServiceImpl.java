package com.example.demo.services.impl;

import com.example.demo.entities.Rules;
import com.example.demo.services.RandomNumberService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
/*
    This service will be used to generate the code by
     fetching as many random numbers from the api: (https://www.random.org)
     as determined by the game rules
 */
public class RandomNumberServiceImpl implements RandomNumberService {
    private final WebClient wc;

    // inject WebClient.Builder to make this more testable via dependency injection
    public RandomNumberServiceImpl(WebClient.Builder builder) {
        this.wc = builder.baseUrl("https://www.random.org").build();
    }
    /*
        generates and returns a code in the form a Integer list of length n
        In the case that Random.org fails/is unavailable, it uses the local generate code function
     */
    public List<Integer> generateCode(Rules rules) {
        List<Integer> code = wc.get().uri(uriBuilder -> uriBuilder.path("/integers/")
                .queryParam("num", rules.getCodeLength())
                .queryParam("min", 0)
                .queryParam("max", rules.getMaxDigit())
                .queryParam("col", 1)
                .queryParam("base", 10)
                .queryParam("format", "plain")
                .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .map(body -> body.trim().lines().map(Integer::parseInt).toList())
                .onErrorResume(x -> {
                    System.err.println("Random.org failed: " + x.getMessage());
                    return Mono.just((localGenerateCode(rules)));
                })
                .block();

        for(int w : code) {
            System.out.println(w);
        }
        return code;
    }

    // fall back in case Random.com is unavailable.
    // randomly generates n integers and adds them to a List that is returned.
    private List<Integer> localGenerateCode(Rules rules) {
        Random rand = new Random();
        List<Integer> res = new ArrayList<>();
        for(int i = 0; i < rules.getCodeLength(); i++) {
            res.add(rand.nextInt(rules.getMaxDigit()+1));
        }

        return res;
    }

}
