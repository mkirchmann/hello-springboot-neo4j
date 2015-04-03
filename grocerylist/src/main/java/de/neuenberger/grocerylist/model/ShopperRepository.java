package de.neuenberger.grocerylist.model;

import org.springframework.data.repository.CrudRepository;

public interface ShopperRepository extends CrudRepository<Shopper, String> {
	Shopper getShopperByName(String name);
}
