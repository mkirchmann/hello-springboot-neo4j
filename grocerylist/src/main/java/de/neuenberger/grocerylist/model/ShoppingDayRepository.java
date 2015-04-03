package de.neuenberger.grocerylist.model;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingDayRepository extends CrudRepository<ShoppingDay, Date> {

}
