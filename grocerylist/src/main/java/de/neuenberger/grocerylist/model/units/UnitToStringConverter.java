package de.neuenberger.grocerylist.model.units;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UnitToStringConverter implements Converter<Unit, String> {

	public String convert(final Unit arg0) {
		return arg0.getTechPrefix() + "." + arg0.toString();
	}

}
