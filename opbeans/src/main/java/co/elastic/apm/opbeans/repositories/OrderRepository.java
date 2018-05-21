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
