package com.polarbookshop.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
record CatalogServiceApplicationTests(WebTestClient testClient, BookRepository bookRepository) {

  @AfterEach
  void tearDown() {
    bookRepository.deleteAll(bookRepository.findAll());
  }

  @Test
  void whenGetRequestWithIdThenBookReturned() {
    String isbn = "1231231230";
    Book book = Book.create(isbn, "Title", "Author", 9.90, "Polarsophia");
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
    Book book = Book.create("1231231230", "Title", "Author", 9.90, "Polarsophia");

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
    Book book = Book.create(isbn, "Title", "Author", 9.90, "Polarsophia");
    Book createdBook =
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
                  assertThat(actualBook.id()).isNotNull();
                  assertThat(actualBook.isbn()).isEqualTo(isbn);
                })
            .returnResult()
            .getResponseBody();
    Book updatedBook =
        new Book(
            createdBook.id(),
            createdBook.isbn(),
            createdBook.title(),
            createdBook.author(),
            7.95,
            createdBook.publisher(),
            createdBook.createdDate(),
            createdBook.lastModifiedDate(),
            createdBook.version());

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
              assertThat(actualBook.price()).isEqualTo(updatedBook.price());
            });
  }

  @Test
  void whenDeleteRequestThenBookDeleted() {
    String isbn = "1231231230";
    Book book = Book.create(isbn, "Title", "Author", 9.90, "Polarsophia");
    testClient.post().uri("/books").bodyValue(book).exchange().expectStatus().isCreated();

    testClient.delete().uri("/books/{isbn}", isbn).exchange().expectStatus().isNoContent();

    testClient.get().uri("/books/{isbn}", isbn).exchange().expectStatus().isNotFound();
  }
}
