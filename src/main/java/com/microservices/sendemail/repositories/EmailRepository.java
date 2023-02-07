package com.microservices.sendemail.repositories;

import com.microservices.sendemail.models.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmailRepository extends JpaRepository<EmailModel, Long> {
    @Query("SELECT e.id FROM EmailModel e WHERE e.statusEmail = 1")
    List<Long> getIdFailedEmails();

    @Query("SELECT e FROM EmailModel e WHERE e.statusEmail = 1")
    List<EmailModel> getFailedEmails();
}
