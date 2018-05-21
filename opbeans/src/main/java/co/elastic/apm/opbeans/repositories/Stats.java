package co.elastic.apm.opbeans.repositories;

public class Stats {
	
	long products;
	
	long customers;
	
	long orders;
	
	Numbers numbers;

	public Stats(long products, long customers, long orders, Numbers numbers) {
		this.products = products;
		this.customers = customers;
		this.orders = orders;
		this.numbers = numbers;
	}

	public long getProducts() {
		return products;
	}

	public long getCustomers() {
		return customers;
	}

	public long getOrders() {
		return orders;
	}

	public Numbers getNumbers() {
		return numbers;
	}
}
