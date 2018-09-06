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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
class APIRestController {

    private final ProductRepository productRepository;

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    @Autowired
    APIRestController(ProductRepository productRepository, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/products")
    ResponseEntity<String> products(HttpServletRequest request) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http", null, request.getLocalAddr(), request.getLocalPort(), "/api/products-remote", null, null);
        String productLists = restTemplate.getForObject(uri.toString(), String.class);
        return ResponseEntity.ok().header("content-type", "application/json").body(productLists);
    }

    @GetMapping(value = "/products-remote")
    Collection<ProductList> productsRemote() {
        return productRepository.findAllList();
    }

    @GetMapping("/products/{productId}")
    ProductDetail product(@PathVariable long productId) {
        return productRepository.getOneDetail(productId);
    }

    @GetMapping("/products/{productId}/customers")
    List<Customer> customerWhoBought(@PathVariable long productId) {
        return customerRepository.findCustomerWhoBoughtProduct(productId);
    }

    @GetMapping("/products/top")
    Collection<TopProduct> topProducts() {
        return productRepository.findTopSales(PageRequest.of(0, 3));
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