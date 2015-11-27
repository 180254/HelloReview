package pl.p.lodz.iis.hr.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Common useful methods for resources (db models/entity).
 */
@Service
public class ResCommonService {

    /**
     * Same as JpaRepository.getOne() but throws ErrorPageException if entity not exist.
     *
     * @param repository entity to be used
     * @param id         id of entity
     * @param <T>        entity class
     * @param <K>        entity id class
     * @return entity read from db
     * @throws ErrorPageException if no entity found
     */
    public <T, K extends Serializable> T getOne(JpaRepository<T, K> repository, K id)
            throws ErrorPageException {

        if (!repository.exists(id)) {
            throw new ErrorPageException(HttpServletResponse.SC_NOT_FOUND);
        }

        return repository.getOne(id);
    }

    /**
     * Same as JpaRepository.getOne() but throws LocalizableErrorRestException if entity not exist.
     *
     * @param repository entity to be used
     * @param id         id of entity
     * @param <T>        entity class
     * @param <K>        entity id class
     * @return entity read from db
     * @throws LocalizableErrorRestException if no entity found
     */
    public <T, K extends Serializable> T getOneForRest(JpaRepository<T, K> repository, K id)
            throws LocalizableErrorRestException {

        if (!repository.exists(id)) {
            throw new LocalizableErrorRestException("NoResource");
        }

        return repository.getOne(id);
    }
}
