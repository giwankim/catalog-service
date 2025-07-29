package com.polarbookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
record BookRepositoryTest(BookRepository bookRepository) {

  @BeforeEach
  void setUp() {
    bookRepository.findAll().forEach(book -> bookRepository.deleteByIsbn(book.isbn()));

    bookRepository.save(new Book("isbn", "title", "author", 9.99));
  }

  @Test
  void findAll() {
    bookRepository.save(new Book("isbn2", "title2", "author2", 19.99));

    var books = (Collection<Book>) bookRepository.findAll();

    assertThat(books).hasSize(2);
  }

  @Test
  void findByIsbn() {
    Book book = bookRepository.findByIsbn("isbn").get();

    assertThat(book.isbn()).isEqualTo("isbn");
  }

  @Test
  void findByIsbnReturnsEmptyOptional() {
    assertThat(bookRepository.findByIsbn("non-existing-isbn")).isEmpty();
  }

  @Test
  void save() {
    bookRepository.save(
        new Book("12345", "Charlie and the Chocolate Factory", "Roald Dahl", 29.99));

    Book book = bookRepository.findByIsbn("12345").get();
    assertThat(book.isbn()).isEqualTo("12345");
  }

  @Test
  void deleteByIsbn() {
    bookRepository.deleteByIsbn("isbn");

    assertThat(bookRepository.existsByIsbn("isbn")).isFalse();
  }
}
