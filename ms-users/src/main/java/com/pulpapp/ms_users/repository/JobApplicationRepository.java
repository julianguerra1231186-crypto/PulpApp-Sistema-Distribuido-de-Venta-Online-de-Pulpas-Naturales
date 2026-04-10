package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findAllByOrderByCreatedAtDesc();
}
