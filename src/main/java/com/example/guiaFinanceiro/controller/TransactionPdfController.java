package com.example.guiaFinanceiro.controller;

import com.example.guiaFinanceiro.dto.TransactionPdfDTO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionPdfController {

    private static final Logger log = LoggerFactory.getLogger(TransactionPdfController.class);

    @PostMapping(value = "/process-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TransactionPdfDTO> processFile(
            @RequestPart("file") MultipartFile file
    ) {
        String contentText = "";

        try {
            // ✔️ VALIDAÇÃO
            String filename = file.getOriginalFilename();

            if (filename == null ||
                    (!filename.toLowerCase().endsWith(".pdf") &&
                            !filename.toLowerCase().matches(".*\\.(png|jpg|jpeg)$"))) {

                log.warn("Arquivo inválido: {}", filename);
                return ResponseEntity.badRequest().build();
            }

            String contentType = file.getContentType();

            // ✔️ PDF
            if ("application/pdf".equals(contentType)) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    contentText = stripper.getText(document);
                }
            }

            // ✔️ IMAGEM (OCR)
            else if (contentType != null && contentType.startsWith("image/")) {

                String tessPath = resolveTessPath();

                log.info("TESSDATA PATH: {}", tessPath);

                // ✔️ VALIDAÇÃO DO ARQUIVO DE IDIOMA
                File trainedData = new File(tessPath + File.separator + "por.traineddata");

                if (!trainedData.exists()) {
                    log.error("por.traineddata NÃO encontrado em {}", tessPath);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new TransactionPdfDTO(0.0, LocalDate.now().toString(), "Erro OCR"));
                }

                Tesseract tesseract = new Tesseract();

                tesseract.setDatapath(tessPath.trim());
                tesseract.setLanguage("por+eng"); // ✔️ fallback
                tesseract.setOcrEngineMode(1);
                tesseract.setPageSegMode(6);

                try (InputStream is = file.getInputStream()) {
                    BufferedImage image = ImageIO.read(is);

                    if (image == null) {
                        log.error("Erro ao ler imagem");
                        return ResponseEntity.badRequest().build();
                    }

                    // ✔️ MELHORIA DE QUALIDADE
                    BufferedImage resized = ImageHelper.getScaledInstance(
                            image,
                            image.getWidth() * 2,
                            image.getHeight() * 2
                    );

                    BufferedImage gray = ImageHelper.convertImageToGrayscale(resized);

                    contentText = tesseract.doOCR(gray);
                }
            }

            log.info("Texto extraído:\n{}", contentText);

            return ResponseEntity.ok(extractData(contentText));

        } catch (Exception e) {
            log.error("Erro no processamento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✔️ RESOLVE CAMINHO AUTOMATICAMENTE
    private String resolveTessPath() {
        // 1. Tenta ler do Ambiente (Melhor prática para Docker/VPS)
        String envPath = System.getenv("TESSDATA_PREFIX");
        if (envPath != null && !envPath.isEmpty()) {
            return envPath;
        }

        String os = System.getProperty("os.name").toLowerCase();

        // 2. Se for Windows (Seu PC Local)
        if (os.contains("win")) {
            return "C:\\Program Files\\Tesseract-OCR\\tessdata";
        }

        // 3. Se for Linux (Docker/VPS) - Ajustado para a versão 5 que seu Docker usa
        return "/usr/share/tesseract-ocr/5/tessdata";
    }

    // ✔️ EXTRAÇÃO INTELIGENTE
    private TransactionPdfDTO extractData(String text) {

        Double maiorValor = 0.0;
        String dataStr = LocalDate.now().toString();
        String descricao = "Transação importada";

        // ✔️ VALORES
        Pattern valorPattern = Pattern.compile(
                "(?:Valor|RS|R\\$|Total|Final)?\\s*[:$]?\\s*(?:RS|R\\$)?\\s*([\\d.,]+)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher valorMatcher = valorPattern.matcher(text);

        while (valorMatcher.find()) {
            try {
                String valorEncontrado = valorMatcher.group(1);

                String clean = valorEncontrado
                        .replace(".", "")
                        .replace(",", ".");

                if (!clean.matches("\\d+\\.\\d{2}")) continue;

                double valor = Double.parseDouble(clean);

                if (valor > maiorValor) {
                    maiorValor = valor;
                }

            } catch (Exception ignored) {}
        }

        // ✔️ DATA
        Pattern dataPattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
        Matcher dataMatcher = dataPattern.matcher(text);

        if (dataMatcher.find()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataStr = LocalDate.parse(dataMatcher.group(1), formatter).toString();
            } catch (Exception e) {
                log.warn("Erro ao converter data");
            }
        }

        // ✔️ DESCRIÇÃO
        String[] linhas = text.split("\\n");

        for (String linha : linhas) {
            if (linha.length() > 10 && linha.length() < 80) {
                descricao = linha.trim();
                break;
            }
        }

        log.info("FINAL -> Valor: {} | Data: {} | Desc: {}", maiorValor, dataStr, descricao);

        return new TransactionPdfDTO(maiorValor, dataStr, descricao);
    }
}