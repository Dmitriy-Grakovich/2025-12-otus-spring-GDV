package ru.diasoft.bookloverbox.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.repository.BookRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final BookRepository bookRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public RepositoryItemReader<Book> bookReader() {
        // Используем findByStatusWithAssociations с JOIN FETCH для предотвращения N+1
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(bookRepository)
                .methodName("findByStatusWithAssociations")
                .arguments(BookStatus.PUBLISHED)
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Book, BookDto> bookProcessor() {
        return book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setDescription(book.getDescription());
            // Author и Genre уже загружены через JOIN FETCH - нет N+1
            dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getFullName() : "Неизвестно");
            dto.setGenreName(book.getGenre() != null ? book.getGenre().getName() : "Без жанра");
            // Reviews не нужны для CSV экспорта - исключаем getAverageRating() и getReviewsCount()
            dto.setViewsCount(book.getViewsCount());
            dto.setDownloadsCount(book.getDownloadsCount());
            dto.setPublishedAt(book.getPublishedAt());
            return dto;
        };
    }

    /**
     * Создает FlatFileItemWriter с timestamp в имени файла.
     * Каждый экспорт создает новый файл: books-export-20260603_140530.csv
     */
    private FlatFileItemWriter<BookDto> createBookWriter(String timestamp) {
        BeanWrapperFieldExtractor<BookDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "title", "authorName", "genreName", 
                                              "viewsCount", "downloadsCount", "publishedAt"});

        DelimitedLineAggregator<BookDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        String filename = "books-export-" + timestamp + ".csv";
        
        return new FlatFileItemWriterBuilder<BookDto>()
                .name("bookWriter")
                .resource(new FileSystemResource(filename))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("ID,Title,Author,Genre,Views,Downloads,Published"))
                .build();
    }

    @Bean
    public Step exportBooksStep(ItemReader<Book> bookReader,
                                 ItemProcessor<Book, BookDto> bookProcessor) {
        // Создаем writer с текущим timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        ItemWriter<BookDto> bookWriter = createBookWriter(timestamp);
        
        return stepBuilderFactory.get("exportBooksStep")
                .<Book, BookDto>chunk(100)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public Job exportBooksJob(Step exportBooksStep) {
        return jobBuilderFactory.get("exportBooksJob")
                .incrementer(new org.springframework.batch.core.launch.support.RunIdIncrementer())
                .start(exportBooksStep)
                .build();
    }
}
