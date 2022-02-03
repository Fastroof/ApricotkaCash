package ua.com.apricortka.apricotkacash.controller;

import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class MainController {

    private static final Logger log = Logger.getLogger(MainController.class);

    @GetMapping(value="/download/text={text}&result={result}")
    public void downloadDocx(@PathVariable String text, @PathVariable String result, HttpServletResponse response) {
        log.info("Downloading docx file");
        try {
            XWPFDocument document = new XWPFDocument(Objects.requireNonNull(MainController.class.getClassLoader().getResourceAsStream("templates/docx/temp.docx")));

            XWPFParagraph p1 = document.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r1 = p1.createRun();
            r1.setText(text + " " + result);

            XWPFParagraph p2 = document.createParagraph();
            p2.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r2 = p2.createRun();
            r2.setText("Дата " + LocalDateTime.now());

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition","attachment; filename=result.docx");
            document.write(response.getOutputStream());
        } catch (Exception e) {
            log.error(e);
        }
    }
}
