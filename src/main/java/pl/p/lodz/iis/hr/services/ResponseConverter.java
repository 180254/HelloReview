package pl.p.lodz.iis.hr.services;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.models.JSONAnswer;
import pl.p.lodz.iis.hr.models.JSONResponse;
import pl.p.lodz.iis.hr.models.courses.Commission;
import pl.p.lodz.iis.hr.models.forms.Input;
import pl.p.lodz.iis.hr.models.response.Answer;
import pl.p.lodz.iis.hr.models.response.Response;
import pl.p.lodz.iis.hr.repositories.CommissionRepository;
import pl.p.lodz.iis.hr.repositories.InputRepository;

import java.util.UUID;

@Service
public class ResponseConverter implements Converter<JSONResponse, Response> {

    private final CommissionRepository commissionRepository;
    private final InputRepository inputRepository;

    @Autowired
    public ResponseConverter(CommissionRepository commissionRepository,
                             InputRepository inputRepository) {

        this.commissionRepository = commissionRepository;
        this.inputRepository = inputRepository;
    }


    @Override
    public @Nullable Response convert(JSONResponse jsonResponse) {
        UUID uuid;

        try {
            uuid = UUID.fromString(jsonResponse.getCommUUID());
        } catch (IllegalArgumentException ignored) {
            return null;
        }

        Commission comm = commissionRepository.findByUuid(uuid);
        if (comm == null) {
            return null;
        }

        Response response = new Response(comm);

        for (JSONAnswer jsonAnswer : jsonResponse.getJsonAnswers()) {
            if (!inputRepository.exists(jsonAnswer.getInputID())) {
                return null;
            }

            Input input = inputRepository.getOne(jsonAnswer.getInputID());

            response.getAnswers().add(new Answer(input, nullIfEmpty(jsonAnswer.getAnswer())));
            response.getAnswers().sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        }

        response.fixRelations();
        return response;
    }

    private String nullIfEmpty(String str) {
        return StringUtils.isBlank(str) ? null : str;
    }
}
