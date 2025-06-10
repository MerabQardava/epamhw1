package com.epam.hw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Training extends JpaRepository<Training, Integer> {
}
