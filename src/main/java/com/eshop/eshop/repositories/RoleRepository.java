package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;


@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Roles findByRole(String role);

}
