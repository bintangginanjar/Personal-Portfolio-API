package com.api.rest.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.rest.portfolio.entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {

}
