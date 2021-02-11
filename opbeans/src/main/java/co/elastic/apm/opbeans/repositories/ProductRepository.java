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

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.elastic.apm.opbeans.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p.id as id, p.sku as sku, p.name as name, p.stock as stock, sum(o.amount) as sold FROM order_lines o LEFT JOIN o.product p GROUP BY p.id order by sold desc")
    List<TopProduct> findTopSales(Pageable pageable);

    @Query("SELECT p.id as id, p.sku as sku, p.name as name, p.stock as stock, t.name AS typeName FROM products p LEFT JOIN p.type as t")
    List<ProductList> findAllList();

    @Query("SELECT sum(o.amount*p.sellingPrice) as revenue,sum(o.amount*p.cost) as cost, sum(o.amount*(p.sellingPrice-p.cost)) as profit FROM order_lines o LEFT JOIN o.product p")
    Numbers getFinancial();

    @Query("select p.id as id, p.sku as sku, p.name as name, p.description as description, p.cost as cost, p.sellingPrice as sellingPrice, p.stock as stock, t.id as typeId, t.name as typeName, (select sum(o.amount) from order_lines o where o.product=p) as sold from products as p  LEFT JOIN p.type as t where p.id=?1")
    Optional<ProductDetail> getOneDetail(long id);
}
