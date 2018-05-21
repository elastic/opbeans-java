package co.elastic.apm.opbeans.repositories;

public interface TopProduct {
	long getId();

	String getSku();

	String getName();

	long getStock();

	long getSold();
}