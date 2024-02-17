package com.alonso.reactive.quarkus;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
				.statusCode(200)
				.body("id", is(carId));
	}

	@Test
	@Order(3)
	public void testGetCarByRange() {
		// Given
		long carId = 1L;

		// When/Then
		given()
				.when().get("/car/{id}", carId)
				.then()
				.statusCode(204);
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
				.statusCode(204);
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
				.statusCode(204);
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
				.statusCode(204);
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
				.statusCode(204);
	}
}