package com.alonso.reactive.quarkus.service;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import com.alonso.reactive.quarkus.model.entity.Car;
import com.alonso.reactive.quarkus.repository.CarRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;


@ApplicationScoped
public class CarService {

	CarRepository carRepository;

	@Inject
	CarService(CarRepository carRepository) {
		this.carRepository = carRepository;
	}

	public Uni<RestResponse<Car>> create(CarRecord car) {
		return carRepository.create(carMapper(car));
	}

	public Uni<RestResponse<Car>> update(Long id, CarRecord car) {
		if(carRepository.findById(id) == null) {
			return carRepository.update(carMapper(car));
		} else {
			//Maybe throw an exception
			return Uni.createFrom().nullItem();
		}
	}

	public Uni<List<Car>> getCarByPrice(Double startPrice, Double finalPrice) {
		return carRepository.findByPrice(BigDecimal.valueOf(startPrice),BigDecimal.valueOf(finalPrice));
	}

	private Car carMapper(CarRecord carRecord) {
		Car carEntity = new Car();
		carEntity.name = carRecord.name();
		carEntity.brand = carRecord.brand();
		carEntity.manufacturingValue = carRecord.manufacturingValue();
		return carEntity;
	}
}