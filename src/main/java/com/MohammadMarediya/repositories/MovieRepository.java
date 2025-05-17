package com.MohammadMarediya.repositories;

import com.MohammadMarediya.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
