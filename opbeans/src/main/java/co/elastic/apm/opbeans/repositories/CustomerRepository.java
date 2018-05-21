package co.elastic.apm.opbeans.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.elastic.apm.opbeans.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@Query("SELECT distinct o.customer FROM order_lines ol left join ol.order o where ol.product.id=?1")
	List<Customer> findCustomerWhoBoughtProduct(long productId);
	
}
