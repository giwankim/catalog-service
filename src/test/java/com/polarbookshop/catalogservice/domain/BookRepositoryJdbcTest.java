package com.polarbookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.polarbookshop.catalogservice.config.JdbcConfig;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest
@Import(JdbcConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
record BookRepositoryJdbcTest(
    BookRepository bookRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {

  @BeforeEach
  void setUp() {
    jdbcAggregateTemplate.insert(Book.create("isbn", "title", "author", 9.99, "Polarsophia"));
  }

  @Test
  void findAll() {
    jdbcAggregateTemplate.insert(Book.create("isbn2", "title2", "author2", 19.99, "Polarsophia"));

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
        Book.create(
            "12345", "Charlie and the Chocolate Factory", "Roald Dahl", 29.99, "Polarsophia"));

    Book book = bookRepository.findByIsbn("12345").get();
    assertThat(book.isbn()).isEqualTo("12345");
  }

  @Test
  void deleteByIsbn() {
    bookRepository.deleteByIsbn("isbn");

    assertThat(bookRepository.existsByIsbn("isbn")).isFalse();
  }
}
