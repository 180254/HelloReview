package pl.p.lodz.iis.hr.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.exceptions.ResourceNotFoundException;

import java.io.Serializable;

@Service
public class ResCommonService {

    public <T, K extends Serializable> T getOne(JpaRepository<T, K> repository, K id)
            throws ResourceNotFoundException {

        if (!repository.exists(id)) {
            throw new ResourceNotFoundException();
        }

        return repository.getOne(id);
    }


    public <T, K extends Serializable> T getOneForRest(JpaRepository<T, K> repository, K id)
            throws LocalizableErrorRestException {

        if (!repository.exists(id)) {
            throw new LocalizableErrorRestException("NoResource");
        }

        return repository.getOne(id);
    }
}
