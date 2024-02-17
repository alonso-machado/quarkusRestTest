package com.alonso.reactive.quarkus.repository;

import com.alonso.reactive.quarkus.model.entity.Car;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.jboss.resteasy.reactive.RestResponse.Status.CREATED;

@ApplicationScoped
@WithTransaction
public class CarRepository implements PanacheRepository<Car> {

	@Transactional
	public Uni<Car> findByName(String name) {
		return find("name", name).firstResult();
	}

	@Transactional
	public Uni<List<Car>> findByBrand(String brandName) {
		return list("brand", brandName);
	}

	@Transactional
	public Uni<Car> getSingle(Long id) {
		return find("id", id).firstResult();
		/*Uni<Car> car = Car.findById(id);
		if(car == null) {
			return Uni.createFrom().nullItem();
		}
		return car;
		*/
	}

	@Transactional
	public PanacheQuery<PanacheEntityBase> getAll() {
		return Car.findAll(Sort.by("name"));
	}

	@Transactional
	public Uni<RestResponse<Car>> create(Car car) {
		return Panache.withTransaction(car::persist).replaceWith(RestResponse.status(CREATED, car));
	}

	@Transactional
	public Uni<RestResponse<Car>> update(Car car) {
		return Panache.withTransaction(car::persist).replaceWith(RestResponse.status(CREATED, car));
	}

	@Transactional
	public Uni<Boolean> deleteSingle(Long id) {
		return Car.deleteById(id);
	}

	@Transactional
	public Uni<List<Car>> findByPrice(BigDecimal startPrice, BigDecimal finalPrice) {
		return list("manufacturingValue BETWEEN :startPrice AND :finalPrice", Parameters.with("startPrice", startPrice).and("finalPrice", finalPrice));
	}
}
