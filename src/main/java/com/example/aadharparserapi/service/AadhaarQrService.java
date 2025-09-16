package com.example.aadharparserapi.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AadhaarQrService {

    public String decodeQrXmlFromImage(InputStream imageInputStream) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
        if (bufferedImage == null) {
            throw new IOException("Unsupported image or corrupted file");
        }

        LuminanceSource src = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(src));

        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    public Map<String, String> parseAadhaarXmlSecurely(String xmlText) throws Exception {
        if (xmlText == null || xmlText.isBlank()) {
            throw new IllegalArgumentException("XML text is empty");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Secure XML processing to prevent XXE and entity expansion
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setExpandEntityReferences(false);
        factory.setXIncludeAware(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        try (ByteArrayInputStream input = new ByteArrayInputStream(xmlText.getBytes(StandardCharsets.UTF_8))) {
            Document doc = builder.parse(input);
            Element root = doc.getDocumentElement();
            Map<String, String> data = new HashMap<>();
            // Common attributes present in Aadhaar QR XML (varies by version)
            putIfPresent(root, data, "uid");
            putIfPresent(root, data, "name");
            putIfPresent(root, data, "yob");
            putIfPresent(root, data, "gender");
            putIfPresent(root, data, "co");
            putIfPresent(root, data, "house");
            putIfPresent(root, data, "street");
            putIfPresent(root, data, "vtc");
            putIfPresent(root, data, "po");
            putIfPresent(root, data, "dist");
            putIfPresent(root, data, "state");
            putIfPresent(root, data, "pc");
            putIfPresent(root, data, "dob");
            putIfPresent(root, data, "loc");
            putIfPresent(root, data, "country");
            return data;
        }
    }

    private void putIfPresent(Element root, Map<String, String> data, String attr) {
        String value = root.getAttribute(attr);
        if (value != null && !value.isBlank()) {
            data.put(attr, value);
        }
    }
}


