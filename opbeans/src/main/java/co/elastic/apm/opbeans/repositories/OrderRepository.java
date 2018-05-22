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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.elastic.apm.opbeans.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o.id as id, o.createdAt as createdAt, c.fullName as customerName FROM orders o LEFT JOIN o.customer as c")
    List<OrderList> findAllList();

    @Query("SELECT o.id as id, o.createdAt as createdAt, c.fullName as customerName, c.id as customerId FROM orders o LEFT JOIN o.customer as c where o.id=?1")
    OrderDetail getOneDetail(long id);
}
