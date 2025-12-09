package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.service.AuthorService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    
    private final AuthorDao authorDao;
    
    @Override
    public List<Author> getAllAuthors() {
        return authorDao.findAll();
    }
    
    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorDao.findById(id);
    }
    
    @Override
    @Transactional
    public Author createAuthor(String firstName, String lastName, Integer age) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setAge(age);
        return authorDao.save(author);
    }
    
    @Override
    @Transactional
    public Author updateAuthor(Long id, String firstName, String lastName, Integer age) {
        Author author = authorDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + id));
        
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setAge(age);
        
        authorDao.update(author);
        return author;
    }
    
    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        authorDao.deleteById(id);
    }
    
    @Override
    public Optional<Author> findAuthorByFullName(String firstName, String lastName) {
        return authorDao.findByFullName(firstName, lastName);
    }
}