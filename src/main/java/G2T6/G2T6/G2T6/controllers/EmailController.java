package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.models.EmailTemplate;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.services.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notification")
@Slf4j
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping(value="/textemail",consumes = "application/json", produces = "application/json")
    public String sendEmail(@RequestBody EmailTemplate emailTemplate) {
        try {
            log.info("Sending Simple Text Email....");

            emailService.sendTextEmail(emailTemplate);
            return "Email Sent!";
        } catch (Exception ex) {
            return "Error in sending email: " + ex;
        }
    }


    @PostMapping(value="/attachemail",consumes = "multipart/form-data")
    public String sendEmailWithAttachment(@RequestPart(value = "file") MultipartFile file) {
        try {
            log.info("Sending Attachment Email....");
            emailService.sendEmailWithAttachment(file);
            return "Email Sent!";
        } catch (Exception ex) {
            return "Error in sending email: " + ex;
        }
    }


}