package ru.diasoft.bookloverbox.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.transaction.PlatformTransactionManager;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.repository.BookRepository;

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
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(bookRepository)
                .methodName("findByStatus")
                .arguments(BookStatus.PUBLISHED)
                .sorts(Map.of("publishedAt", Sort.Direction.DESC))
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Book, BookDto> bookProcessor() {
        return book -> {
            BookDto dto = new BookDto();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setDescription(book.getDescription());
            dto.setAuthorName(book.getAuthor().getFullName());
            dto.setGenreName(book.getGenre() != null ? book.getGenre().getName() : "Без жанра");
            dto.setAverageRating(book.getAverageRating());
            dto.setReviewsCount(book.getReviewsCount());
            dto.setViewsCount(book.getViewsCount());
            dto.setDownloadsCount(book.getDownloadsCount());
            dto.setPublishedAt(book.getPublishedAt());
            return dto;
        };
    }

    @Bean
    public FlatFileItemWriter<BookDto> bookWriter() {
        BeanWrapperFieldExtractor<BookDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "title", "authorName", "genreName", 
                                              "averageRating", "reviewsCount", "viewsCount", 
                                              "downloadsCount", "publishedAt"});

        DelimitedLineAggregator<BookDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<BookDto>()
                .name("bookWriter")
                .resource(new FileSystemResource("books-export.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("ID,Title,Author,Genre,Rating,Reviews,Views,Downloads,Published"))
                .build();
    }

    @Bean
    public Step exportBooksStep(ItemReader<Book> bookReader,
                                 ItemProcessor<Book, BookDto> bookProcessor,
                                 ItemWriter<BookDto> bookWriter) {
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
                .start(exportBooksStep)
                .build();
    }
}
