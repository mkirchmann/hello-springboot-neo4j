package de.neuenberger.grocerylist;

import java.util.Date;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;

import de.neuenberger.grocerylist.model.Shopper;
import de.neuenberger.grocerylist.model.ShopperRepository;
import de.neuenberger.grocerylist.model.ShoppingDay;
import de.neuenberger.grocerylist.model.ShoppingDayRepository;
import de.neuenberger.grocerylist.model.ShoppingItem;
import de.neuenberger.grocerylist.model.ShoppingItemRepository;
import de.neuenberger.grocerylist.model.ShoppingItemToShop;
import de.neuenberger.grocerylist.model.ShoppingItemToShopRepository;
import de.neuenberger.grocerylist.model.units.PieceUnit;
import de.neuenberger.grocerylist.model.units.StringToUnitConverter;
import de.neuenberger.grocerylist.model.units.UnitToStringConverter;
import de.neuenberger.grocerylist.model.units.WeightUnit;

@SpringBootApplication
@EnableAutoConfiguration
public class Application implements CommandLineRunner {

	@Configuration
	@EnableNeo4jRepositories(basePackages = ApplicationConfig.MODEL_PACKAGE)
	static class ApplicationConfig extends Neo4jConfiguration {

		static final String MODEL_PACKAGE = "de.neuenberger.grocerylist.model";

		@Autowired
		StringToUnitConverter stringToUnitConverter;
		@Autowired
		UnitToStringConverter unitToStringConverter;

		public ApplicationConfig() {
			setBasePackage(MODEL_PACKAGE);
		}

		@Bean
		GraphDatabaseService graphDatabaseService() {
			return new SpringRestGraphDatabase("http://localhost:7474/db/data", "neo4j", "groc1234");
		}

		@Override
		protected ConversionService neo4jConversionService() throws Exception {
			final ConverterRegistry neo4jConversionService = (ConverterRegistry) super.neo4jConversionService();
			neo4jConversionService.addConverter(stringToUnitConverter);
			neo4jConversionService.addConverter(unitToStringConverter);
			return (ConversionService) neo4jConversionService;
		}
	}

	@Autowired
	ShoppingItemRepository shoppingItemRepository;

	@Autowired
	ShoppingDayRepository shoppingDayRepository;

	@Autowired
	ShopperRepository shopperRepository;

	@Autowired
	ShoppingItemToShopRepository shoppingItemToShopRepository;

	@Autowired
	GraphDatabase graphDatabase;

	public void run(final String... args) throws Exception {

		final Shopper shopper1 = new Shopper("Michael");
		final Shopper shopper2 = new Shopper("Leah");

		final ShoppingDay shoppingDay1_1 = new ShoppingDay(shopper1, new Date());
		final ShoppingDay shoppingDay1_2 = new ShoppingDay(shopper1, new Date(System.currentTimeMillis() - 24l * 60l
				* 60l * 1000l));
		final ShoppingDay shoppingDay2_1 = new ShoppingDay(shopper2, new Date(System.currentTimeMillis() - 24l * 60l
				* 60l * 1000l));
		final ShoppingDay shoppingDay2_2 = new ShoppingDay(shopper2, new Date(System.currentTimeMillis() - 2l * 24l
				* 60l * 60l * 1000l));

		final ShoppingItem zucker = new ShoppingItem("Zucker");
		final ShoppingItem mehl = new ShoppingItem("Mehl");
		final ShoppingItem eier = new ShoppingItem("Eier");
		final ShoppingItem butter = new ShoppingItem("Butter");
		final ShoppingItem backPulver = new ShoppingItem("Backpulver");

		System.out.println("Before linking up with Neo4j...");

		final Transaction tx = graphDatabase.beginTx();
		try {

			shopperRepository.save(shopper1);
			shopperRepository.save(shopper2);

			for (final ShoppingItem shoppingItem : new ShoppingItem[] { zucker, mehl, eier, butter, backPulver }) {
				System.out.println(shoppingItem);
				shoppingItemRepository.save(shoppingItem);
				System.out.println(shoppingItem);
			}

			shoppingDayRepository.save(shoppingDay1_1);
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay1_1, zucker, 1, WeightUnit.KILOGRAMM));
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay1_1, eier, 6, PieceUnit.PIECE));

			shoppingDayRepository.save(shoppingDay1_2);
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay1_2, mehl, 2, WeightUnit.KILOGRAMM));
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay1_2, zucker, 2, WeightUnit.KILOGRAMM));
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay1_2, eier, 10, PieceUnit.PIECE));

			shoppingDayRepository.save(shoppingDay2_1);
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay2_1, butter, 1, PieceUnit.PIECE));
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay2_1, backPulver, 1, PieceUnit.PIECE));
			shoppingDayRepository.save(shoppingDay2_2);
			shoppingItemToShopRepository.save(new ShoppingItemToShop(shoppingDay2_2, mehl, 1, WeightUnit.KILOGRAMM));

			tx.success();
		} finally {
			tx.close();
		}
	}

	public static void main(final String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}