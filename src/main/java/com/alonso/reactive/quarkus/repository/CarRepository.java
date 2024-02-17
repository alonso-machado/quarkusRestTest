package com.alonso.reactive.quarkus.repository;

import com.alonso.reactive.quarkus.model.entity.Car;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.jboss.resteasy.reactive.RestResponse.Status.*;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;

@ApplicationScoped
public class CarRepository implements PanacheRepository<Car> {

	@Inject
	Mutiny.SessionFactory sf;

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
		String nativeQuery = "SELECT * FROM car WHERE id = " + id;
		return Car.findById(id);
		//return find("id = :id", Parameters.with("id", id)).firstResult();
		//return sf.withTransaction((s, t) -> s.find(Car.class, id));
		/*return Panache.getSession().onItem()
				.transformToUni(session ->
						session.createNativeQuery(nativeQuery, Car.class)
								.getSingleResultOrNull());*/
		/*return sf.withSession( session -> session
				.createNativeQuery( nativeQuery, Car.class ).getSingleResultOrNull()
		);*/
	}

	@Transactional
	public Uni<List<Car>> getAll() {
		return Car.findAll().list();
	}

	@Transactional
	public Uni<RestResponse<Car>> create(Car car) {
		return Panache.withTransaction(car::persist).replaceWith(RestResponse.status(CREATED, car));
	}

	//PUT from https://github.com/quarkusio/quarkus-quickstarts/blob/main/hibernate-reactive-quickstart/src/main/java/org/acme/hibernate/reactive/FruitMutinyResource.java

	@Transactional
	public Uni<RestResponse<Car>> updateWithoutPanache(Long id, Car car) {
		return sf.withTransaction((s, t) -> s.find(Car.class, id)
						.onItem().ifNull().failWith(new WebApplicationException("Car missing from database.", NOT_FOUND))
						.onItem().ifNotNull().invoke(entity -> {
							entity.manufacturingValue = car.manufacturingValue;
							entity.name = car.name;
							entity.brand = car.brand;
							entity.description = car.description;
						}))
				.map(entity -> RestResponse.status(ACCEPTED, entity));
	}

	//Put2 https://github.com/quarkusio/quarkus-quickstarts/blob/main/hibernate-reactive-panache-quickstart/src/main/java/org/acme/hibernate/orm/panache/FruitResource.java
	@Transactional
	public Uni<RestResponse<Car>> updatePanache(Long id, Car car) {
		return Panache.withTransaction(() -> Car.<Car>findById(id)
				.onItem().ifNotNull().invoke(entity -> {
					entity.name = car.name;
					entity.brand = car.brand;
					entity.description = car.description;
					entity.manufacturingValue = car.manufacturingValue;
				})
				.onItem().ifNull().failWith(new WebApplicationException("Car missing from database.", NOT_FOUND))
		).onItem().ifNotNull().transform(entity -> RestResponse.status(ACCEPTED, entity));
	}


	@Transactional
	public Uni<RestResponse> deleteSingle(Long id) {
		return Panache.withTransaction(() -> Car.deleteById(id))
				.map(deleted -> deleted
						? RestResponse.status(NO_CONTENT)
						: RestResponse.status(NOT_FOUND));
	}

	@Transactional
	public Uni<List<Car>> findByPrice(BigDecimal startPrice, BigDecimal finalPrice) {
		return list("manufacturingValue BETWEEN :startPrice AND :finalPrice", Parameters.with("startPrice", startPrice).and("finalPrice", finalPrice))
				.onItem().ifNull().failWith(new WebApplicationException("Car missing from database.", NOT_FOUND));
	}
}
