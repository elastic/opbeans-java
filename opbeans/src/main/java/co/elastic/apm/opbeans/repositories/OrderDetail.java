package co.elastic.apm.opbeans.repositories;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface OrderDetail {
	long getId();

	@JsonProperty("customer_name")
	String getCustomerName();

	@JsonProperty("customer_id")
	Long getCustomerId();

	@JsonProperty("created_at")
	Date getCreatedAt();
}