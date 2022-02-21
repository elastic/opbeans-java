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
package co.elastic.apm.opbeans.controllers;

import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.opbeans.model.Customer;
import co.elastic.apm.opbeans.model.Order;
import co.elastic.apm.opbeans.repositories.CustomerRepository;
import co.elastic.apm.opbeans.repositories.OrderDetail;
import co.elastic.apm.opbeans.repositories.OrderList;
import co.elastic.apm.opbeans.repositories.OrderRepository;
import co.elastic.apm.opbeans.repositories.ProductDetail;
import co.elastic.apm.opbeans.repositories.ProductList;
import co.elastic.apm.opbeans.repositories.ProductRepository;
import co.elastic.apm.opbeans.repositories.Stats;
import co.elastic.apm.opbeans.repositories.TopProduct;
import com.fasterxml.jackson.databind.JsonNode;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
class APIRestController {

    private static final Logger logger = LoggerFactory.getLogger(APIRestController.class);
    private static final int TOP_SALES_SIZE = 3;

    private static final Tracer tracer = GlobalOpenTelemetry.get().getTracer("co.elastic.apm:opbeans");

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Autowired
    APIRestController(ProductRepository productRepository, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping(value = "/products")
    @CaptureSpan("Annotation products span")
    Collection<ProductList> products() {
        ElasticApm.currentSpan().setLabel("foo", "bar");
        return productRepository.findAllList();
    }

    @GetMapping("/products/{productId}")
    ProductDetail product(@PathVariable long productId) {
        Optional<ProductDetail> result;

        Span span = tracer.spanBuilder("OpenTelemetry product span")
                .setAttribute("product.id", productId)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            result = productRepository.getOneDetail(productId);
        } finally {
            span.end();
        }

        // Spring MVC uses exceptions to return a 404, thus we keep that out of the OTel span to avoid
        // any confusion as it's not an actual server error but a way to return a client error.
        return result.orElseThrow(notFound());
    }

    @GetMapping("/products/{productId}/customers")
    List<Customer> customerWhoBought(@PathVariable long productId) {
        return customerRepository.findCustomerWhoBoughtProduct(productId);
    }

    @GetMapping("/products/top")
    Collection<TopProduct> topProducts() {
        logger.info("Finding top {} sales", TOP_SALES_SIZE);
        return productRepository.findTopSales(PageRequest.of(0, TOP_SALES_SIZE));
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return customerRepository.findAll();
    }

    @GetMapping("/customers/{customerId}")
    Customer customer(@PathVariable long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(notFound());
    }

    @GetMapping("/orders")
    Collection<OrderList> orders() {
        Span span = tracer.spanBuilder("OpenTelemetry orders")
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            return orderRepository.findAllList();
        } finally {
            span.end();
        }
    }

    @GetMapping("/orders/{orderId}")
    OrderDetail order(@PathVariable long orderId) {
        Span span = tracer.spanBuilder("OpenTelemetry get order")
                .setAttribute("order.id", orderId)
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            return orderRepository.getOneDetail(orderId)
                    .orElseThrow(notFound());
        } finally {
            span.end();
        }

    }

    @PostMapping("/orders/")
    OrderDetail createOrder(@RequestBody JsonNode orderJson) {
        Span span = tracer.spanBuilder("OpenTelemetry create order")
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            Customer customer = customerRepository.findById(orderJson.get("customer_id").asLong())
                    .orElseThrow(notFound());

            Order savedOrder = saveOrder(customer);
            return order(savedOrder.getId());
        } finally {
            span.end();
        }
    }

    private Order saveOrder(Customer customer) {
        Span span = tracer.spanBuilder("OpenTelemetry save order")
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            Order order = new Order();
            order.setCreatedAt(Date.from(Instant.now()));
            order.setCustomer(customer);

            return orderRepository.save(order);
        } finally {
            span.end();
        }

    }

    @GetMapping("/stats")
    Stats stats() {
        return new Stats(productRepository.count(), customerRepository.count(), orderRepository.count(), productRepository.getFinancial());
    }

    private Supplier<ResponseStatusException> notFound() {
        return () -> new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}