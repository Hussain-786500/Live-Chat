package com.example.demo.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request) {

        try {
            // 📁 Upload directory
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 📌 Save file
            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            file.transferTo(new File(filePath));

            // 🌐 Auto base URL (NO hardcoded IP)
            String baseUrl = request.getScheme() + "://" +
                    request.getServerName() + ":" +
                    request.getServerPort();

            // 🔗 QR CONTENT (IMPORTANT FIX)
            String qrText = baseUrl + "/uploads/" + fileName;

            // 🔳 QR generation
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrText,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    image.setRGB(x, y,
                            bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF
                    );
                }
            }

            // 💾 Save QR
            String qrPath = uploadDir + "qr.png";
            ImageIO.write(image, "PNG", new File(qrPath));

            return "result";

        } catch (Exception e) {
            e.printStackTrace();
            return "result";
        }
    }

    // 🖼 Serve QR image
    @GetMapping("/qr-image")
    public ResponseEntity<Resource> showQrImage() {

        try {
            Path path = Paths.get(
                    System.getProperty("user.dir") + "/uploads/qr.png"
            );

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 📂 Serve uploaded files
    @GetMapping("/uploads/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {

        try {
            Path path = Paths.get(
                    System.getProperty("user.dir") + "/uploads/" + filename
            );

            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}