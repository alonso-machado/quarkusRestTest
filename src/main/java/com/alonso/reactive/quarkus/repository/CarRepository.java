package com.alonso.reactive.quarkus.repository;

import com.alonso.reactive.quarkus.exception.CarNotFoundException;
import com.alonso.reactive.quarkus.model.entity.Car;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.status;

@ApplicationScoped
public class CarRepository implements PanacheRepository<Car> {

	@Transactional
	public Uni<Car> findByName(String name) {
		return find("name", name).firstResult();
	}

	@Transactional
	public Uni<List<Car>> findByBrand(String brandName) {
		return list("brand", brandName);
	}

	@WithSession
	public Uni<Car> getSingleById(Long id) {
		return Car.<Car>findById(id)
				.onItem().ifNull().failWith(CarNotFoundException::new)
				.onItem().ifNotNull().transformToUni(e -> Uni.createFrom().item(e));
	}

	@Transactional
	public Uni<List<Car>> getAll() {
		return Car.findAll().list();
	}

	@Transactional
	public Uni<List<Car>> getAllPaged(Integer pageIndex, Integer pageSize) {
		return Car.findAll().page(pageIndex, pageSize).list();
	}

	@Transactional
	public Uni<RestResponse<Car>> create(Car car) {
		return Panache.withTransaction(car::persist).replaceWith(status(CREATED, car));
	}

	//Put https://github.com/quarkusio/quarkus-quickstarts/blob/main/hibernate-reactive-panache-quickstart/src/main/java/org/acme/hibernate/orm/panache/FruitResource.java
	@Transactional
	public Uni<RestResponse<Car>> updatePanache(Long id, Car car) {
		return Panache.withTransaction(() -> Car.<Car>findById(id)
				.onItem().ifNotNull().invoke(entity -> {
					entity.name = car.name;
					entity.brand = car.brand;
					entity.description = car.description;
					entity.manufacturingValue = car.manufacturingValue;
				})
				.onItem().ifNull().failWith(CarNotFoundException::new)
		).onItem().ifNotNull().transform(entity -> status(ACCEPTED, entity));
	}


	@Transactional
	public Uni<RestResponse> deleteSingle(Long id) {
		return Panache.withTransaction(() -> Car.deleteById(id))
				.map(deleted -> Boolean.TRUE.equals(deleted)
						? status(NO_CONTENT)
						: status(NOT_FOUND));
	}

	@Transactional
	public Uni<List<Car>> findByPrice(BigDecimal startPrice, BigDecimal finalPrice) {
		return list("manufacturingValue BETWEEN :startPrice AND :finalPrice", Parameters.with("startPrice", startPrice).and("finalPrice", finalPrice))
				.onItem().ifNull().failWith(CarNotFoundException::new);
	}
}
