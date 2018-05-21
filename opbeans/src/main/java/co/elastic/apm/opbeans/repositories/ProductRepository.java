package co.elastic.apm.opbeans.repositories;

import java.util.List;

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
	ProductDetail getOneDetail(long id);
}
