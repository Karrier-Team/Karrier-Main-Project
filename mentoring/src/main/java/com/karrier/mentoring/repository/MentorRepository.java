package com.karrier.mentoring.repository;

import com.karrier.mentoring.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, String> {
    Mentor findByEmail(String email);

    List<String> findEmailByMajor(String major);
}