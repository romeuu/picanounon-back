package com.picanounon.back.repository;

import com.picanounon.back.model.AuthProvider;
import com.picanounon.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}