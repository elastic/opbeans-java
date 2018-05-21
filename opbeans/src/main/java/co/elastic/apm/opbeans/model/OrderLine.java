package co.elastic.apm.opbeans.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity(name = "order_lines")
public class OrderLine {

	@EmbeddedId
	private OrderLineId orderId;

	@ManyToOne
	@MapsId("orderId")
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne
	@MapsId("productId")
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	private int amount;

	public OrderLineId getOrderId() {
		return orderId;
	}

	public void setOrderId(OrderLineId orderId) {
		this.orderId = orderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Embeddable
	public class OrderLineId implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "order_id")
		private long orderId;

		@Column(name = "product_id")
		private long productId;

		public long getOrderId() {
			return orderId;
		}

		public void setOrderId(long orderId) {
			this.orderId = orderId;
		}

		public long getProductId() {
			return productId;
		}

		public void setProductId(long productId) {
			this.productId = productId;
		}
	}
}
