package pl.p.lodz.iis.hr.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Service
public class ResCommonService {

    public <T, K extends Serializable> T getOne(JpaRepository<T, K> repository, K id)
            throws ErrorPageException {

        if (!repository.exists(id)) {
            throw new ErrorPageException(HttpServletResponse.SC_NOT_FOUND);
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
