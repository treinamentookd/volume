package br.com.tecnisys.volumebackend;

public class Arquivo {

    public Arquivo(String nome, String caminhoDownload) {
        this.nome = nome;
        this.caminhoDownload = caminhoDownload;
    }

    private String nome;
    private String caminhoDownload;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminhoDownload() {
        return caminhoDownload;
    }

    public void setCaminhoDownload(String caminhoDownload) {
        this.caminhoDownload = caminhoDownload;
    }
}
