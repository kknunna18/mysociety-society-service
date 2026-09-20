package com.mysociety.society.outbox;
import org.springframework.stereotype.Component;
@Component public class TransactionalOutbox {
    public void enqueue(DomainEvent event) { /* Kafka publisher may consume this transactional boundary in deployment. */ }
}
