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

import java.util.Collection;
import java.util.List;

import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.opentracing.ElasticApmTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.elastic.apm.opbeans.model.Customer;
import co.elastic.apm.opbeans.repositories.CustomerRepository;
import co.elastic.apm.opbeans.repositories.OrderDetail;
import co.elastic.apm.opbeans.repositories.OrderList;
import co.elastic.apm.opbeans.repositories.OrderRepository;
import co.elastic.apm.opbeans.repositories.ProductDetail;
import co.elastic.apm.opbeans.repositories.ProductList;
import co.elastic.apm.opbeans.repositories.ProductRepository;
import co.elastic.apm.opbeans.repositories.Stats;
import co.elastic.apm.opbeans.repositories.TopProduct;

@RestController
@RequestMapping("/api")
class APIRestController{

    private static final Logger logger = LoggerFactory.getLogger(APIRestController.class);
    private static final int TOP_SALES_SIZE = 3;

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private Tracer tracer;

    @Autowired
    APIRestController(ProductRepository productRepository, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        tracer = new ElasticApmTracer();
    }

    @GetMapping(value = "/products")
    @CaptureSpan("Annotation products span")
    Collection<ProductList> products() {
        ElasticApm.currentSpan().addTag("foo", "bar");
        return productRepository.findAllList();
    }

    @GetMapping("/products/{productId}")
    ProductDetail product(@PathVariable long productId) {
        final Span span = tracer.buildSpan("OpenTracing product span")
                .withTag("productId", Long.toString(productId))
                .start();
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            return productRepository.getOneDetail(productId);
        } finally {
            span.finish();
        }
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
        return customerRepository.getOne(customerId);
    }

    @GetMapping("/orders")
    Collection<OrderList> orders() {
        return orderRepository.findAllList();
    }

    @GetMapping("/orders/{orderId}")
    OrderDetail order(@PathVariable long orderId) {
        return orderRepository.getOneDetail(orderId);
    }

    @GetMapping("/stats")
    Stats stats() {
        return new Stats(productRepository.count(), customerRepository.count(), orderRepository.count(), productRepository.getFinancial());
    }

}