package de.neuenberger.grocerylist.model;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingItemRepository extends CrudRepository<ShoppingItem, String> {

	ShoppingItem findByName(String name);

}
