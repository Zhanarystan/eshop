package com.eshop.eshop.services;

import com.eshop.eshop.entities.Comment;
import com.eshop.eshop.entities.Items;
import com.eshop.eshop.entities.Roles;
import com.eshop.eshop.entities.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;


public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);

    Users getUserById(Long id);

    Users createUser(Users user);

    Users updateUserByAdmin(Users users);

    Roles createRole(Roles role);
    List<Users> getAllUsers();

    List<Roles> getAllRoles();

    Roles getRoleById(Long id);

    void deleteUser(Users user);

    Users updateUser(Users user);

    void deleteRole(Roles role);
    List<Comment> getAllCommentsByItem(Items item);
    void deleteComment(Comment comment);
    Comment addComment(Comment comment);
    Comment saveComment(Comment comment);
    Comment getComment(Long id);

}
