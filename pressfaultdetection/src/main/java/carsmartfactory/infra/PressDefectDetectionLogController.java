package carsmartfactory.infra;

import carsmartfactory.domain.*;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/pressDefectDetectionLogs")
@Transactional
public class PressDefectDetectionLogController {

    @Autowired
    PressDefectDetectionLogRepository pressDefectDetectionLogRepository;
}
//>>> Clean Arch / Inbound Adaptor
