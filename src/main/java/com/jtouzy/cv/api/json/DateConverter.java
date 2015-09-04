package com.jtouzy.cv.api.json;

import java.time.LocalDateTime;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

public class DateConverter implements Converter<LocalDateTime> {
	@Override
	public void serialize(LocalDateTime object, ObjectWriter writer, Context ctx) 
	throws Exception {
		writer.writeString(object.toString());
	}
	@Override
	public LocalDateTime deserialize(ObjectReader reader, Context ctx) 
	throws Exception {
		return LocalDateTime.parse(reader.valueAsString());
	}
}
