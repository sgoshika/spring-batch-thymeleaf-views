package com.infybuzz.writer;

import com.infybuzz.model.StudentCSV;
import com.infybuzz.model.StudentJDBC;
import com.infybuzz.model.StudentJSON;
import com.infybuzz.model.StudentResponse;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<StudentResponse> {

    @Override
    public void write(List<? extends StudentResponse> items) throws Exception {
//        System.out.println("****** Chunk Oriented Step: Writer ********");
        System.out.println("****** Printing Student Data ********");

        items.stream().forEach(System.out::println);
    }
}
