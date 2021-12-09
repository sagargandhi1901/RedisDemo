package com.example.demoredis.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person implements Serializable {

    private long id;
    private String name;
    private int age;
    private double creditScore;
    private boolean isSeniorCitizen;

}
