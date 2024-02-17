package com.alonso.reactive.quarkus.model.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/* //Using this to avoid PanacheEntity @GeneratedValue that Default to AUTO and break the Tests */
@Entity
@Table(name = "car")
@Cacheable
public class Car extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long id;

	@Column(length = 25)
	public String name;
	public String brand;

	@DecimalMin(value = "0.0", inclusive = false)
	@DecimalMax(value = "15000000.00", inclusive = false)
	public BigDecimal manufacturingValue;

	public String description;

}
