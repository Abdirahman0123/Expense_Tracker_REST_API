package com.ExpenseTracker;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype:jackson-datatype-jsr310;

@SpringBootApplication
public class ExpenseTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApplication.class, args);
		

        ObjectMapper o = new ObjectMapper();
        o.registerModule(new JavaTimeModule()); 
        
		// register LocalDate for serialisaton
		/*ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);*/
        
		/*JsonMapper jsonMapper = new JsonMapper();
		jsonMapper.registerModule(new JavaTimeModule());*/
        //ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.registerModule(new Jdk8Module());
        
        // register Optional	 for serialisaton
        //ObjectMapper mapperr = new ObjectMapper();
        //mapperr.addMixInAnnotations(Optional.class, Optional.class);
	}

	 /*@Autowired
	  void configureObjectMapper(final ObjectMapper mapper) {
	   mapper.registerModule(new ParameterNamesModule())
	  .registerModule(new Jdk8Module())
	  .registerModule(new JavaTimeModule());
	  }*/
	
	
}
