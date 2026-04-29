package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.repository.AuthorRepository;
import ru.diasoft.spring.service.AuthorService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
    
    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }
    
    @Override
    @Transactional
    public Author createAuthor(String firstName, String lastName, Integer age) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setAge(age);
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public Author updateAuthor(Long id, String firstName, String lastName, Integer age) {
        return authorRepository.findById(id)
                .map(author -> {
                    author.setFirstName(firstName);
                    author.setLastName(lastName);
                    author.setAge(age);
                    return authorRepository.save(author);
                })
                .orElseGet(() -> {
                    // Create a new author without setting the ID
                    Author newAuthor = new Author();
                    newAuthor.setFirstName(firstName);
                    newAuthor.setLastName(lastName);
                    newAuthor.setAge(age);
                    return authorRepository.save(newAuthor);
                });
    }
    
    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
    
    @Override
    public Optional<Author> findAuthorByFullName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}