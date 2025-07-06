package com.linktune.api.Repository;


import com.linktune.api.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortCode(String shortCode);

}