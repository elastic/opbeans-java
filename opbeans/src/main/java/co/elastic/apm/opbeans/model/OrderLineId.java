package co.elastic.apm.opbeans.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (orderId ^ (orderId >>> 32));
            result = prime * result + (int) (productId ^ (productId >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OrderLineId other = (OrderLineId) obj;
            if (orderId != other.orderId)
                return false;
            if (productId != other.productId)
                return false;
            return true;
        }

    }