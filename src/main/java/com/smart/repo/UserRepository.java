package com.smart.repo;

import com.smart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    @Query("select user from User user where user.email = :email")
//    public User getUserByUserName(@Param("email") String email);

    public User findByEmail(String email);

}
