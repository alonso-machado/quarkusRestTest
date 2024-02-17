package com.alonso.reactive.quarkus;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;

@QuarkusTest
public class CarResourceTest {

	@Test
	public void testListAllCars() {
		//List all, should have all 3 fruits the database has initially:
		Response response = given()
				.when()
				.get("/")
				.then()
				.statusCode(200)
				.contentType("application/json")
				.extract().response();
		assertThat(response.jsonPath().getList("name"), Matchers.containsInAnyOrder(List.of("F150", "Enzo", "Uno Mille")));
	}

	@Test
	public void testEntityNotFoundForDelete() {
		given()
				.when()
				.delete("/car/999")
				.then()
				.statusCode(404)
				.body(emptyString());
	}

	@Test
	public void testEntityNotFoundForUpdate() {
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(20000));
		given()
				.when()
				.body(carRecord)
				.contentType("application/json")
				.put("/car/6767")
				.then()
				.statusCode(404)
				.body(emptyString());
	}
}
