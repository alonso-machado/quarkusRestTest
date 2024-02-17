package com.alonso.reactive.quarkus.controller;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import com.alonso.reactive.quarkus.model.entity.Car;
import com.alonso.reactive.quarkus.repository.CarRepository;
import com.alonso.reactive.quarkus.service.CarService;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/car")
@Tag(name = "car")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@WithTransaction
public class CarResource {
	CarRepository carRepository;

	CarService carService;

	@Inject
	CarResource(CarRepository carRepository, CarService carService) {
		this.carRepository = carRepository;
		this.carService = carService;
	}

	@GET
	@Path("/")
	@Operation(summary = "Retrieves all cars")
	public PanacheQuery<PanacheEntityBase> getAll() {
		return carRepository.getAll();
	}

	@GET
	@Path("/{id}")
	@Operation(summary = "Returns a car")
	@APIResponse(
			responseCode = "200",
			description = "Return a car for a given identifier (ID)",
			content = @Content(
					mediaType = APPLICATION_JSON,
					schema = @Schema(implementation = Car.class, required = true)
			)
	)
	public Uni<Car> getSingle(@Parameter(name = "id", required = true) @PathParam("id") Long id) {
		return carRepository.getSingle(id);
	}

	@GET
	@Path("/{name}")
	@Operation(summary = "Retrieves a car by name")
	public Uni<Car> getCarByName(@Parameter(name = "name", required = true) @PathParam("name") String name) {
		return carRepository.findByName(name);
	}

	@GET
	@Path("/brand/{name}")
	@Operation(summary = "Retrieves a cars by brand name")
	public Uni<List<Car>> getCarByBrand(@Parameter(name = "name", required = true) @PathParam("name") String name) {
		return carRepository.findByBrand(name);
	}

	@GET
	@Path("/price-range/{startPrice}/{finalPrice}")
	@Operation(summary = "Retrieves a cars by price")
	public Uni<List<Car>> getCarByPrice(@Parameter(name = "startPrice", required = true) @PathParam("startPrice") @Min(value = 0) Double startPrice,
	                                    @Parameter(name = "finalPrice", required = true) @PathParam("finalPrice") @Max(value = 15000000) Double finalPrice) {
		return carService.getCarByPrice(startPrice, finalPrice);
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Operation(summary = "Create a new car")
	@Transactional
	@NonBlocking
	public Uni<RestResponse<Car>> create(@Valid @NotNull CarRecord car) {
		return carService.create(car);
	}

	@PUT
	@Path("/{id}")
	@Consumes(APPLICATION_JSON)
	@Operation(summary = "Update a existing car")
	@Transactional
	@NonBlocking
	public Uni<RestResponse<Car>> update(@Parameter(name = "id", required = true) @PathParam("id") Long id, @Valid @NotNull CarRecord car) {
		return carService.update(id, car);
	}

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete a car")
	@Transactional
	@NonBlocking
	public Uni<Boolean> deleteSingle(@Parameter(name = "id", required = true) @PathParam("id") Long id) {
		return carRepository.deleteSingle(id);
	}
}
