package com.phatpl.learnvocabulary.repositories;

import com.phatpl.learnvocabulary.filters.UserFilter;
import com.phatpl.learnvocabulary.models.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface UserRepository extends BaseRepository<User, UserFilter, Integer> {
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
    Optional<User> findById(Integer id);
}
