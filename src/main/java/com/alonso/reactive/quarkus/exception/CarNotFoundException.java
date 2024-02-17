package com.alonso.reactive.quarkus.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class CarNotFoundException extends WebApplicationException {

	public CarNotFoundException(){
		super("Car missing from database.", Response.Status.NOT_FOUND);
	}

}
