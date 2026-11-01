package com.krishimitra.soil.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoilParameters {

    // pH: unitless, valid range 0–14
    private Double pH;

    // Electrical conductivity: dS/m
    private Double electricalConductivity;

    // Organic carbon: percentage
    private Double organicCarbon;

    // Primary nutrients: kg/ha
    private Double nitrogen;
    private Double phosphorus;
    private Double potassium;
    private Double sulphur;

    // Micronutrients: mg/kg
    private Double zinc;
    private Double iron;
    private Double manganese;
    private Double copper;
    private Double boron;
}