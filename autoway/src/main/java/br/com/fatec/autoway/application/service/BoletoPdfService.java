package br.com.fatec.autoway.application.service;

import br.com.fatec.autoway.domain.model.Boleto;
import br.com.fatec.autoway.domain.model.Passagem;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class BoletoPdfService {

    private static final Color LOGO_COLOR = new Color(78, 9, 103); // Roxo #4E0967
    private static final Color SECTION_BG = new Color(245, 245, 245); // Cinza claro para sombra
    private static final Color LINE_COLOR = LOGO_COLOR;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] gerarPdfBoleto(Boleto boleto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // =======================
            // Logo dentro de retângulo roxo arredondado
            // =======================
            PdfPTable logoTable = new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.setSpacingAfter(15);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBackgroundColor(LOGO_COLOR);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setPadding(15);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            try {
                InputStream logoStream = new ClassPathResource("static/logo.png").getInputStream();
                Image logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(140, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                System.out.println("Logo não encontrada: " + e.getMessage());
            }

            logoTable.addCell(logoCell);
            document.add(logoTable);

            // =======================
            // Título centralizado
            // =======================
            Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, LOGO_COLOR);
            Paragraph titulo = new Paragraph("Boleto AutoWay", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // =======================
            // Informações do Boleto com borda arredondada e sombra
            // =======================
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new int[]{3, 7});
            infoTable.setSpacingAfter(15);
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Font subTitulo = new Font(Font.HELVETICA, 12, Font.BOLD, LOGO_COLOR);
            Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);

            infoTable.addCell(createStyledCell("Período:", subTitulo));
            infoTable.addCell(createStyledCell(boleto.dataInicio().format(DATE_FORMAT) + " a " + boleto.dataFim().format(DATE_FORMAT), normal));

            infoTable.addCell(createStyledCell("Data de Emissão:", subTitulo));
            infoTable.addCell(createStyledCell(boleto.dataEmissao().format(DATE_FORMAT), normal));

            infoTable.addCell(createStyledCell("Data de Vencimento:", subTitulo));
            infoTable.addCell(createStyledCell(boleto.dataVencimento().format(DATE_FORMAT), normal));

            infoTable.addCell(createStyledCell("Status:", subTitulo));
            infoTable.addCell(createStyledCell(capitalizeFirstLetter(boleto.statusPagamento().name()), normal));

            infoTable.addCell(createStyledCell("Valor Total:", subTitulo));
            infoTable.addCell(createStyledCell("R$ " + boleto.valorTotal(), normal));

            document.add(infoTable);

            // =======================
            // Tabela de Passagens com borda arredondada e linha roxa
            // =======================
            PdfPTable tabela = new PdfPTable(2);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new int[]{3, 2});
            tabela.setSpacingBefore(10);
            tabela.setSpacingAfter(20);

            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

            PdfPCell c1 = new PdfPCell(new Phrase("Data da Passagem", headerFont));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(LOGO_COLOR);
            c1.setPadding(8);
            tabela.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase("Valor (R$)", headerFont));
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setBackgroundColor(LOGO_COLOR);
            c2.setPadding(8);
            tabela.addCell(c2);

            for (Passagem p : boleto.passagens()) {
                PdfPCell cellData = new PdfPCell(new Phrase(p.data().format(DATE_FORMAT), normal));
                cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellData.setPadding(6);
                tabela.addCell(cellData);

                PdfPCell cellValor = new PdfPCell(new Phrase(formatDecimal(p.valor()), normal));
                cellValor.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellValor.setPadding(6);
                tabela.addCell(cellValor);
            }

            document.add(tabela);

            // =======================
            // Instruções de pagamento
            // =======================
            Paragraph instrucoesTitle = new Paragraph("Instruções de Pagamento", subTitulo);
            instrucoesTitle.setSpacingAfter(5);
            document.add(instrucoesTitle);

            Paragraph instrucoes = new Paragraph(
                    "- Pagamento até a data de vencimento.\n" +
                            "- Após o vencimento, juros de 1% ao dia.\n" +
                            "- Mantenha este boleto em local seguro.",
                    normal
            );
            instrucoes.setSpacingAfter(25);
            document.add(instrucoes);

            // =======================
            // Código de Barras maior, centralizado e espaçado
            // =======================
            String codigoBarras = gerarCodigoBarrasDeterministico(boleto);
            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(codigoBarras);
            barcode128.setCodeType(Barcode128.CODE128);

            Image barcodeImage = barcode128.createImageWithBarcode(writer.getDirectContent(), null, null);
            barcodeImage.setAlignment(Element.ALIGN_CENTER);
            barcodeImage.scalePercent(200); // Maior e mais visível
            document.add(barcodeImage);

            document.add(Chunk.NEWLINE);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do boleto", e);
        }
    }

    private PdfPCell createStyledCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.setBackgroundColor(SECTION_BG);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        return cell;
    }

    private String formatDecimal(double valor) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(valor);
    }

    private String gerarCodigoBarrasDeterministico(Boleto boleto) {
        // Combina campos únicos do boleto em uma string
        String chave = boleto.dataInicio().toString() + boleto.dataFim().toString()
                + boleto.valorTotal() + boleto.dataEmissao().toString();
        // Converte para hash ou número fixo
        return Integer.toHexString(chave.hashCode()).toUpperCase();
    }


    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        text = text.toLowerCase();
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
