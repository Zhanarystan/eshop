package com.eshop.eshop.services.impl;

import com.eshop.eshop.entities.Comment;
import com.eshop.eshop.entities.Items;
import com.eshop.eshop.entities.Roles;
import com.eshop.eshop.entities.Users;
import com.eshop.eshop.repositories.CommentsRepository;
import com.eshop.eshop.repositories.RoleRepository;
import com.eshop.eshop.repositories.UserRepository;
import com.eshop.eshop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users myUser = userRepository.findByEmail(s);

        if(myUser!=null){
            System.out.println(myUser.getEmail()+" "+myUser.getPassword());
            User secUser = new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;
        }

        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users createUser(Users user) {
        Users checkUser = userRepository.findByEmail(user.getEmail());
        if(checkUser==null){
            Roles role = roleRepository.findByRole("ROLE_USER");
            if(role!=null){
                ArrayList<Roles> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                return userRepository.save(user);
            }
        }
        return null;
    }

    @Override
    public Users updateUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users updateUserByAdmin(Users user) {
        Users checkUser = userRepository.findByEmail(user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));


        return userRepository.save(user);

    }

    @Override
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Roles getRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public Users getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public Roles createRole(Roles role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Roles role) {
        roleRepository.delete(role);
    }
    @Override
    public List<Comment> getAllCommentsByItem(Items item) {
        return commentsRepository.findAllByItem(item);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentsRepository.delete(comment);
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentsRepository.save(comment);
    }

    @Override
    public Comment getComment(Long id) {
        return commentsRepository.getOne(id);
    }
}
