package co.elastic.apm.opbeans.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ProductList {
	long getId();

	String getSku();

	String getName();

	long getStock();

	@JsonProperty("type_name")
	String getTypeName();
}