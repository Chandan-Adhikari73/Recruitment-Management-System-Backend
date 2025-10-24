package com.example.recruitment.repository;
import com.example.recruitment.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobRepository extends JpaRepository<Job, Long> { }
