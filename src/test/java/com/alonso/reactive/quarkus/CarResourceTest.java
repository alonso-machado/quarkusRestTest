package com.alonso.reactive.quarkus;

import com.alonso.reactive.quarkus.model.dto.CarRecord;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
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
		CarRecord carRecord = new CarRecord("Fusion", "Ford", BigDecimal.valueOf(99000));
		given()
				.when()
				.body(carRecord)
				.contentType("application/json")
				.put("/car/6767")
				.then()
				.statusCode(RestResponse.Status.NOT_FOUND.getStatusCode())
				.body(emptyString());
	}
}
