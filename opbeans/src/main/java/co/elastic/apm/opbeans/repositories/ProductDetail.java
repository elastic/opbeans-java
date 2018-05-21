package co.elastic.apm.opbeans.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ProductDetail {
	long getId();

	String getSku();

	String getName();

	String getDescription();

	double getCost();

	@JsonProperty("selling_price")
	double getSellingPrice();

	long getStock();

	@JsonProperty("type_id")
	long getTypeId();

	@JsonProperty("type_name")
	String getTypeName();

	long getSold();
}