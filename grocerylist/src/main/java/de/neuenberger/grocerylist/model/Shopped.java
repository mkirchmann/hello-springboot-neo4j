package de.neuenberger.grocerylist.model;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity
public class Shopped {
	@GraphId
	private Long id;

	Shopper shopper;
	ShoppingDay shoppingDay;
}
