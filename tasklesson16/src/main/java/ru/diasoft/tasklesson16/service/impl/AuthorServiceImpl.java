package ru.diasoft.tasklesson16.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.repository.AuthorRepository;
import ru.diasoft.tasklesson16.service.AuthorService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

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
        author.setLastName(lastName);
        author.setAge(age);
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public Optional<Author> updateAuthor(Long id, String firstName, String lastName, Integer age) {
        return authorRepository.findById(id)
                .map(author -> {
                    author.setFirstName(firstName);
                    author.setLastName(lastName);
                    author.setAge(age);
                    return authorRepository.save(author);
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