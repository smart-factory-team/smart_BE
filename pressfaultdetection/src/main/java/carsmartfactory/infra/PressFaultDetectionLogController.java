package carsmartfactory.infra;

import carsmartfactory.domain.*;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/pressFaultDetectionLogs")
@Transactional
public class PressFaultDetectionLogController {

    @Autowired
    PressFaultDetectionLogRepository pressFaultDetectionLogRepository;
}
//>>> Clean Arch / Inbound Adaptor
