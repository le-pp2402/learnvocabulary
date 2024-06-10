package com.phatpl.learnvocabulary.repositories;

import com.phatpl.learnvocabulary.filters.BaseFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, FT extends BaseFilter, ID extends Integer> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
//    Page<T> findWithFilter(FT ft, Pageable pageable);
    Optional<T> findById(Integer id);
}
