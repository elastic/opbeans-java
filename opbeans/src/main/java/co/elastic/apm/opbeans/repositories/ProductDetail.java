/*-
 * #%L
 * Opbeans Java Demo Application
 * %%
 * Copyright (C) 2018 the original author or authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

	Long getSold();
}