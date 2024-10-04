package com.smart.repo;

import com.smart.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface contactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact as contact where contact.user.id = :userid")
    public List<Contact> getContactByUser(@Param("userid") int userid);
}
