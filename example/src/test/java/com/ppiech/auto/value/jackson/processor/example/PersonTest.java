package com.ppiech.auto.value.jackson.processor.example;

import com.ppiech.auto.value.jackson.JacksonAuto;
import com.ppiech.auto.value.jackson.processor.example.gson.Address;
import com.ppiech.auto.value.jackson.processor.example.gson.Person;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PersonTest {
    @Test
    public void testGson() throws Exception {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String birthdate = "2007-11-11";
        Date date = df.parse(birthdate);

        JacksonAuto auto = new JacksonAuto.Builder()
                .registerJsonMapperFactory(SampleMapperFactory.create())
                .build();

        Person person = Person.builder()
                .name("Piasy")
                .gender(1)
                .age(23)
                .friends(Arrays.asList("jon", "mary", "jo"))
                .birthdate(date)
                .address(Address.create("street", "city"))
                .build();
        String json = "{\"name\":\"Piasy\",\"gender\":1,\"age\":23,\"friends\":[\"jon\",\"mary\",\"jo\"],\"birthdate\":\"" + birthdate + "\",\"address\":{\"street-name\":\"street\",\"city\":\"city\"}}";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        auto.mapperFor(Person.class).serialize(person, outputStream);
        String jsonString = outputStream.toString();

        Assert.assertEquals(json, outputStream.toString());

        Person fromJson = auto.mapperFor(Person.class).parse(json);
        Assert.assertEquals("Piasy", fromJson.name());
        Assert.assertEquals(23, fromJson.age());
        Assert.assertEquals(1, fromJson.gender());
        Assert.assertEquals(date, fromJson.birthdate());
    }
}
