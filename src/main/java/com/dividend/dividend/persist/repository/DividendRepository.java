package com.dividend.dividend.persist.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dividend.dividend.persist.entity.Company;
import com.dividend.dividend.persist.entity.Dividend;

@Repository
public interface DividendRepository extends JpaRepository<Dividend,Long> {
	List<Dividend> findAllByCompanyId(Long companyId);

	boolean existsByCompanyIdAndLocalDateTime(Long companyId, LocalDateTime localDateTime);
}
