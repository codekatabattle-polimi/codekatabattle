package it.polimi.codekatabattle.services.impl;

import it.polimi.codekatabattle.services.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class CrudServiceImpl<M> implements CrudService<M> {

    private final JpaRepository<M, Long> repository;

    public CrudServiceImpl(JpaRepository<M, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<M> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Page<M> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    public Optional<M> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public M save(M model) {
        return this.repository.save(model);
    }

    @Override
    public M update(M user) {
        return this.repository.save(user);
    }

    @Override
    public Optional<M> delete(Long userId) {
        return this.repository.findById(userId).map(user -> {
            this.repository.delete(user);
            return user;
        });
    }

}
