package com.infybuzz.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<Long> {
    @Override
    public void write(List<? extends Long> list) throws Exception {
        System.out.println("****** Chunk Oriented Step: Writer ********");

        list.stream().forEach(System.out::println);
    }
}
