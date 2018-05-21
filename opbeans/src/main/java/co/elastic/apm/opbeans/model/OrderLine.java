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
