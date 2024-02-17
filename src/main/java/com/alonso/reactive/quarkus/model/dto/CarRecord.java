package com.alonso.reactive.quarkus.model.dto;

import java.math.BigDecimal;

public record CarRecord(String name, String brand, BigDecimal manufacturingValue) {
}
