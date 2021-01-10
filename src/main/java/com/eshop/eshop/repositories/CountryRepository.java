package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CountryRepository extends JpaRepository<Countries, Long> {

}
