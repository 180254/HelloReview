package pl.p.lodz.iis.hr.services;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;

import java.io.Serializable;

@Service
public class ResCommonService {

    public <T, K extends Serializable> T getOne(JpaRepository<T, K> repository, K id) {
        if (!repository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        return repository.getOne(id);
    }


    public @Nullable <T, K extends Serializable> T getOneForJSON(JpaRepository<T, K> repository, K id) {
        if (!repository.exists(id)) {
            return null;
        }

        return repository.getOne(id);
    }
}
