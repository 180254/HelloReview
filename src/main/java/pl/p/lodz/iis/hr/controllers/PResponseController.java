package pl.p.lodz.iis.hr.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.p.lodz.iis.hr.exceptions.ErrorPageException;
import pl.p.lodz.iis.hr.exceptions.LocalizableErrorRestException;
import pl.p.lodz.iis.hr.models.JSONResponse;
import pl.p.lodz.iis.hr.models.courses.CommissionStatus;
import pl.p.lodz.iis.hr.models.response.Response;
import pl.p.lodz.iis.hr.repositories.ResponseRepository;
import pl.p.lodz.iis.hr.services.LocaleService;
import pl.p.lodz.iis.hr.services.ResponseConverter;
import pl.p.lodz.iis.hr.services.ResponseValidator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
class PResponseController {

    private final ObjectMapper objectMapper;
    private final ResponseConverter responseConverter;
    private final ResponseValidator responseValidator;
    private final ResponseRepository responseRepository;
    private final LocaleService localeService;

    @Autowired
    PResponseController(ObjectMapper objectMapper,
                        ResponseConverter responseConverter,
                        ResponseValidator responseValidator,
                        ResponseRepository responseRepository,
                        LocaleService localeService) {
        this.objectMapper = objectMapper;
        this.responseConverter = responseConverter;
        this.responseValidator = responseValidator;
        this.responseRepository = responseRepository;
        this.localeService = localeService;
    }

    @RequestMapping(
            value = "/p/response",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public List<String> response(@ModelAttribute("response") String response)
            throws ErrorPageException, LocalizableErrorRestException {

        JSONResponse jsonResponse;
        try {
            jsonResponse = objectMapper
                    .readerFor(JSONResponse.class)
                    .readValue(response.trim());

        } catch (IOException e) {
            throw (ErrorPageException)
                    new ErrorPageException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).initCause(e);
        }

        Response response1 = responseConverter.convert(jsonResponse);
        if (response1 == null) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (response1.getCommission().getStatus() != CommissionStatus.UNFILLED) {
            throw new ErrorPageException(HttpServletResponse.SC_BAD_REQUEST);
        }

        responseValidator.validate(response1);

        response1.getCommission().setResponse(response1);
        response1.getCommission().setStatus(CommissionStatus.FILLED);
        responseRepository.save(response1);

        return localeService.getAsList("p.response.done");
    }
}
