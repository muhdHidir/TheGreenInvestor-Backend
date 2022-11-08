package G2T6.G2T6.G2T6.services.email;


import java.text.SimpleDateFormat;
import java.util.Date;

import G2T6.G2T6.G2T6.models.EmailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private EmailService emailService;
    private EmailTemplate emailTemplate;

   @Scheduled(cron = "0 0 1 * * MON")
    //Test
    // @Scheduled(fixedRate = 60000, initialDelay = 20000)
    public void sendingEmail() {
        log.info("The email is sent at {}", dateFormat.format(new Date()));

        try {
            emailService.sendTextEmail(emailTemplate);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
