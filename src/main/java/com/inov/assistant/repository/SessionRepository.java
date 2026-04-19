package com.inov.assistant.repository;

import com.inov.assistant.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    @Query("{ '_id': ?0 }")
    Optional<Session> findByIdWithMessages(String id);
}