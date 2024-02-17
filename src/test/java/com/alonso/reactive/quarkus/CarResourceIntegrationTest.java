package com.alonso.reactive.quarkus;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class CarResourceIntegrationTest {

	@Test
	@Order(1)
	public void testGetAllCars() {
		// When/Then
		given()
				.when().get("/car/")
				.then()
				.statusCode(200);
	}

	@Test
	@Order(2)
	public void testGetCarById() {
		// Given
		long carId = 1L;

		// When/Then
		given()
				.when().get("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.body("id", is(carId));
	}

	@Test
	@Order(3)
	public void testGetCarByRange() {
		// Given
		Double startPrice = 9500.0;
		Double finalPrice = 110000.0;

		// When/Then
		given()
				.when().get("/car/price-range/{startPrice}/{finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode());
	}

	@Test
	@Order(4)
	public void testGetCarByBrandName() {
		// Given
		long carId = 1L;

		// When/Then
		given()
				.when().get("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(5)
	public void testDeleteCar() {
		// Given
		long carId = 1L;

		// When/Then
		given().header("Content-type", "application/json")
				.when().delete("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(6)
	public void testUpdateCar() {
		// Given
		long carId = 1L;
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(70000));

		// When/Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().put("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@Order(7)
	public void testPostCar() {
		// Given
		CarRecord carRecord = new CarRecord("Ka", "Ford", BigDecimal.valueOf(25000));

		// When / Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().post("/car")
				.then()
				.statusCode(RestResponse.Status.CREATED.getStatusCode());
	}

	@Test
	@Order(8)
	public void testGetCarByRangeNotPossible() {
		// Given
		Double startPrice = 0.0;
		Double finalPrice = 1.0;

		// When/Then
		given()
				.when().get("/car/price-range/{startPrice}/{finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.body("", equalTo(Collections.emptyList()));
	}
}