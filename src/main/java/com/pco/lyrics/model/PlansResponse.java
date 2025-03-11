package com.pco.lyrics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlansResponse {
    private Links links;
    private List<Plan> data;
    private Meta meta;
} 