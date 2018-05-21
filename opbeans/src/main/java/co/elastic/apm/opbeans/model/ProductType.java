package co.elastic.apm.opbeans.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity(name="product_types")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductType {
	
	@Id
    @GeneratedValue
	private long id;

	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
