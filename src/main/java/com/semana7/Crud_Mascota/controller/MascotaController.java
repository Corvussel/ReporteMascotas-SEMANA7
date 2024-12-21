package com.semana7.Crud_Mascota.controller;

import com.semana7.Crud_Mascota.model.Mascota;
import com.semana7.Crud_Mascota.service.MascotaService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import java.io.OutputStream;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public String listarMascotas(Model model) {
        List<Mascota> lista = mascotaService.listarMascotas();
        model.addAttribute("mascotas", lista);
        return "lista_mascotas";
    }

    @GetMapping("/nuevo")
    public String formularioMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "formulario_mascotas";
    }

    @PostMapping("/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        mascotaService.guardarMascota(mascota);
        return "redirect:/mascotas";
    }

    @GetMapping("/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.obtenerMascotaPorId(id);
        model.addAttribute("mascota", mascota);
        return "formulario_mascotas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return "redirect:/mascotas";
    }

    // Exportar a Excel
    @GetMapping("/exportar/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mascotas");
            
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Edad");
            header.createCell(3).setCellValue("Especie");
            header.createCell(4).setCellValue("Raza");
            
            List<Mascota> mascotas = mascotaService.listarMascotas();
            int rowNum = 1;
            for (Mascota mascota : mascotas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(mascota.getId());
                row.createCell(1).setCellValue(mascota.getNombre());
                row.createCell(2).setCellValue(mascota.getEdad());
                row.createCell(3).setCellValue(mascota.getEspecie());
                row.createCell(4).setCellValue(mascota.getRaza());
            }
            
            workbook.write(response.getOutputStream());
        }
    }

    // Exportar a PDF
    @GetMapping("/exportar/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.pdf");

        try (OutputStream out = response.getOutputStream()) {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            try (Document document = new Document(pdfDoc)) {
                Table table = new Table(5);
                table.addCell("ID");
                table.addCell("Nombre");
                table.addCell("Edad");
                table.addCell("Especie");
                table.addCell("Raza");
                
                List<Mascota> mascotas = mascotaService.listarMascotas();
                for (Mascota mascota : mascotas) {
                    table.addCell(String.valueOf(mascota.getId()));
                    table.addCell(mascota.getNombre());
                    table.addCell(String.valueOf(mascota.getEdad()));
                    table.addCell(mascota.getEspecie());
                    table.addCell(mascota.getRaza());
                }
                
                document.add(table);
            }
        }
    }
}
