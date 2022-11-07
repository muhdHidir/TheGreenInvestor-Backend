package G2T6.G2T6.G2T6.services.email;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import G2T6.G2T6.G2T6.models.Article;
import G2T6.G2T6.G2T6.models.EmailTemplate;
import G2T6.G2T6.G2T6.models.Option;
import G2T6.G2T6.G2T6.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ArticleService articleService;

    @Value("yogesh.adhi.narayan@gmail.com, yanutron@gmail.com")
    private String attchEmailAddr;
    //testing email Addresses
    private String[] textEmailAddr = {"yogesh.adhi.narayan@gmail.com", "yanutron@gmail.com"};
    private int index = 0;

    public void setIndex(int index){
        this.index = index;
    }


    public void sendTextEmail(EmailTemplate emailTemplate) throws MessagingException, IOException {
        List<Article> articles = articleService.listArticles();
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        setIndex(index++);
        try {
            int recipientSize = textEmailAddr.length;
            for (int i = 0; i < recipientSize; i++) {

                helper.setFrom(new InternetAddress("greeninvestor45@gmail.com", "Green Investor"));
                helper.setTo(textEmailAddr[i]);
                helper.setSubject("Weekly News");
                helper.setText("<h1>" + "Here is your weekly news" + "</h1>" + "<br>" + "<p>" + articles.get(index).getBody() + "</p>" + "<br>" + "<p>" + articles.get(index).getArticle() + "</p>", true);
                javaMailSender.send(msg);
            }
            index++;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendEmailWithAttachment(MultipartFile multipartFile) throws MessagingException, IOException {

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);

        try {
            if (attchEmailAddr.contains(",")) {
                String[] emails = attchEmailAddr.split(",");
                int recipientSize = emails.length;
                for (int i = 0; i < recipientSize; i++) {
                    helper.setTo(emails[i]);
                    helper.setSubject("Attachment File !");
                    helper.setText("<h1>" + "Find the Attachment file" + "</h1>", true);
                    InputStreamSource attachment = new ByteArrayResource(multipartFile.getBytes());

                    helper.addAttachment(multipartFile.getOriginalFilename(), attachment);
                    javaMailSender.send(msg);
                }

            } else {
                helper.setTo(attchEmailAddr);
                helper.setSubject("Attachment File !");
                // default = text/plain
                // true = text/html
                helper.setText("<h1>" + "Find the Attachment file" + "</h1>", true);
                InputStreamSource attachment = new ByteArrayResource(multipartFile.getBytes());

                helper.addAttachment(multipartFile.getOriginalFilename(), attachment);
                javaMailSender.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
