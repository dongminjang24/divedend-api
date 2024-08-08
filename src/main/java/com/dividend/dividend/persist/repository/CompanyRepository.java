package com.dividend.dividend.persist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dividend.dividend.persist.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {

	boolean existsByTicker(String ticker);

	Optional<Company> findByName(String name);

	Optional<Company> findByTicker(String ticker);

	List<Company> findByNameStartingWithIgnoreCase(String s, PageRequest pageRequest);
}
