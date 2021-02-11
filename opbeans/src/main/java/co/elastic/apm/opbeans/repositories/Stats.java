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

public class Stats {

    private final Long products;

    private final Long customers;

    private final Long orders;

    private final Numbers numbers;

    public Stats(Long products, Long customers, Long orders, Numbers numbers) {
        this.products = products;
        this.customers = customers;
        this.orders = orders;
        this.numbers = numbers;
    }

    @JsonProperty("products")
    public Long getProducts() {
        return products;
    }

    @JsonProperty("customers")
    public Long getCustomers() {
        return customers;
    }

    @JsonProperty("orders")
    public Long getOrders() {
        return orders;
    }

    @JsonProperty("numbers")
    public Numbers getNumbers() {
        return numbers;
    }
}
