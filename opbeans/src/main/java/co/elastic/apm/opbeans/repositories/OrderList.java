package co.elastic.apm.opbeans.repositories;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface OrderList {
	long getId();

	@JsonProperty("customer_name")
	String getCustomerName();

	@JsonProperty("created_at")
	Date getCreatedAt();
}