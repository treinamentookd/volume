package br.com.tecnisys.volumebackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController("/")
public class Endpoint {

    @Value("${caminho}")
    private String caminho;

    @GetMapping("/ola")
    public String ola(){
        return "Ola mundo";
    }

    @PostMapping("/upload")
    public ResponseEntity uploadToLocalFileSystem(@RequestParam("arquivo") MultipartFile file) throws Exception {
        File diretorio = new File(caminho);
        if (!diretorio.exists()){
            diretorio.mkdirs();
        }

        String arquivo = StringUtils.cleanPath(file.getOriginalFilename());
        Path path = Paths.get(caminho + "/" + arquivo);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        String fileDownloadUri = getFileDownloadUri(arquivo);
        return ResponseEntity.ok(fileDownloadUri);
    }

    private String getFileDownloadUri(String arquivo) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(arquivo)
                .toUriString();
    }

    @GetMapping("/listar")
    public ResponseEntity listar(){
        File diretorio = new File(caminho);
        if (!diretorio.exists()){
            diretorio.mkdirs();
        }
        Set<Arquivo> arquivos =  Stream.of(new File(caminho).listFiles())
                .filter(file -> !file.isDirectory())
                .map(file-> new Arquivo(file.getName(), getFileDownloadUri(file.getName())))
                .collect(Collectors.toSet());
        return  ResponseEntity.ok(arquivos);

    }

    @GetMapping("/download/{arquivo:.+}")
    public ResponseEntity downloadFileFromLocal(@PathVariable String arquivo) throws MalformedURLException {
        Path path = Paths.get(caminho + "/" + arquivo);
        Resource resource = null;

        resource = new UrlResource(path.toUri());


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
