package com.polarbookshop.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
record CatalogServiceApplicationTests(WebTestClient testClient, BookRepository bookRepository) {

  @BeforeEach
  void setUp() {
    bookRepository.findAll().forEach(book -> bookRepository.deleteByIsbn(book.isbn()));
  }

  @Test
  void whenGetRequestWithIdThenBookReturned() {
    String isbn = "1231231230";
    Book book = new Book(isbn, "Title", "Author", 9.90);
    testClient.post().uri("/books").bodyValue(book).exchange().expectStatus().isCreated();

    testClient
        .get()
        .uri("/books/{isbn}", isbn)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook).isNotNull();
              assertThat(actualBook.isbn()).isEqualTo(isbn);
            });
  }

  @Test
  void whenPostRequestThenBookCreated() {
    Book book = new Book("1231231230", "Title", "Author", 9.90);

    testClient
        .post()
        .uri("/books")
        .bodyValue(book)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook).isNotNull();
              assertThat(actualBook.isbn()).isEqualTo(book.isbn());
            });
  }

  @Test
  void whenPutRequestThenBookUpdated() {
    String isbn = "1231231230";
    Book book = new Book(isbn, "Title", "Author", 9.90);
    testClient.post().uri("/books").bodyValue(book).exchange().expectStatus().isCreated();

    Book updatedBook = new Book(isbn, "Updated Title", "Updated Author", 7.95);

    testClient
        .put()
        .uri("/books/{isbn}", isbn)
        .bodyValue(updatedBook)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Book.class)
        .value(
            actualBook -> {
              assertThat(actualBook).isNotNull();
              assertThat(actualBook.title()).isEqualTo(updatedBook.title());
              assertThat(actualBook.author()).isEqualTo(updatedBook.author());
              assertThat(actualBook.price()).isEqualTo(updatedBook.price());
            });
  }

  @Test
  void whenDeleteRequestThenBookDeleted() {
    String isbn = "1231231230";
    Book book = new Book(isbn, "Title", "Author", 9.90);
    testClient.post().uri("/books").bodyValue(book).exchange().expectStatus().isCreated();

    testClient.delete().uri("/books/{isbn}", isbn).exchange().expectStatus().isNoContent();

    testClient.get().uri("/books/{isbn}", isbn).exchange().expectStatus().isNotFound();
  }
}
