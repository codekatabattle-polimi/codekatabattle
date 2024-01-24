package it.polimi.codekatabattle.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudService<M> {

    List<M> findAll();

    Page<M> findAll(Pageable pageable);

    Optional<M> findById(Long id);

    M save(M user);

    M update(M user);

    Optional<M> delete(Long id);

}
