package com.dividend.dividend.persist.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dividend.dividend.persist.entity.Dividend;
import com.dividend.dividend.persist.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);
}
