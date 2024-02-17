package com.alonso.reactive.quarkus;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEmptyString.emptyString;

@QuarkusTest
public class CarResourceTest {

	@Test
	@Order(0)
	public void testListAllCars() {
		//List all, should have all some of the cars the database has initially (import.sql)
		Response response = given()
				.when()
				.get("/car/")
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Diablo", "Enzo", "Uno Mille", "x1"));
	}

	@Test
	@Order(1)
	public void testEntityNotFoundForDelete() {
		given()
				.when()
				.delete("/car/999")
				.then()
				.statusCode(RestResponse.Status.NOT_FOUND.getStatusCode())
				.body(emptyString());
	}

	@Test
	@Order(2)
	public void testEntityNotFoundForUpdate() {
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(99000), "Hatchback");
		given()
				.when()
				.body(carRecord)
				.contentType("application/json")
				.put("/car/6767")
				.then()
				.statusCode(RestResponse.Status.NOT_FOUND.getStatusCode())
				.body(emptyString());
	}

	@Test
	@Order(3)
	public void testEntityNotFoundForGetId() {
		given()
				.when()
				.contentType("application/json")
				.get("/car/6767")
				.then()
				.statusCode(RestResponse.Status.NOT_FOUND.getStatusCode())
				.body(emptyString());
	}

	@Test
	@Order(4)
	public void testEntityNotFoundForGetName() {
		given()
				.when()
				.contentType("application/json")
				.get("/car/name/Onix")
				.then()
				.statusCode(RestResponse.Status.NO_CONTENT.getStatusCode())
				.body(emptyString());
	}

	@Test
	@Order(5)
	public void testEntityFoundForGetName() {
		Response response = given()
				.when()
				.contentType("application/json")
				.get("/car/name/x5")
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().get("name"), Matchers.equalTo("x5"));
		assertThat(response.jsonPath().get("brand"), Matchers.equalTo("BMW"));
	}

	@Test
	@Order(6)
	public void testGetCarByBrandName() {
		// Given
		String carBrand = "Ferrari";

		// When/Then
		Response response = given()
				.when().get("/car/brand/{carBrand}", carBrand)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode()).contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Enzo", "F50"));
		assertThat(response.jsonPath().getList("brand"), Matchers.everyItem(Matchers.equalTo("Ferrari")));
	}

	@Test
	@Order(7)
	public void testGetCarByBrandNameNonExistent() {
		// Given
		String carBrand = "TestNonExistent";

		// When/Then
		given()
				.when().get("/car/brand/{carBrand}", carBrand)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode()).contentType("application/json")
				.body("", equalTo(Collections.emptyList()));
		;
	}

	@Test
	@Order(8)
	public void testUpdateCar() {
		// Given
		long carId = 1L;
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(70000), "Hatchback");

		// When/Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().put("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.ACCEPTED.getStatusCode());
	}

	@Test
	@Order(9)
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
	@Order(10)
	public void testPostCar() {
		// Given
		CarRecord carRecord = new CarRecord("Ka", "Ford", BigDecimal.valueOf(25000), "Mini Car");

		// When / Then
		given().header("Content-type", "application/json")
				.and()
				.body(carRecord)
				.when().post("/car")
				.then()
				.statusCode(RestResponse.Status.CREATED.getStatusCode());
	}

	@Test
	@Order(11)
	public void testGetCarByRangeNotPossible() {
		// Given
		Double startPrice = 0.0;
		Double finalPrice = 1.0;

		// When/Then
		given()
				.when().get("/car/price-range/?startPrice={startPrice}&finalPrice={finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.body("", equalTo(Collections.emptyList()));
	}

	@Test
	@Order(12)
	public void testGetCarByRange() {
		// Given
		Double startPrice = 9500.0;
		Double finalPrice = 110000.0;

		// When/Then
		Response response = given()
				.when().get("/car/price-range/?startPrice={startPrice}&finalPrice={finalPrice}", startPrice, finalPrice)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Uno Mille"));
	}

	@Test
	@Order(13)
	public void testGetCarById() {
		// Given
		Long carId = 3L;

		// When/Then
		Response response = given()
				.when().get("/car/{id}", carId)
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode()).contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().get("id").toString(), Matchers.equalTo(carId.toString()));
	}

	@Test
	@Order(14)
	public void testListAllCarsPaged() {
		//List all, should have all some of the cars the database has initially (import.sql)
		Response response = given()
				.when()
				.get("/car/?pageIndex=0&pageSize=10")
				.then()
				.statusCode(RestResponse.Status.OK.getStatusCode())
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.hasItems("Uno Mille", "x1"));
	}
}
