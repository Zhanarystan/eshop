package com.eshop.eshop.repositories;

import com.eshop.eshop.entities.Comment;
import com.eshop.eshop.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem(Items item);
}
