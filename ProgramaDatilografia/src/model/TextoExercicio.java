package model;

public class TextoExercicio {

    private String conteudo;
    private String dificuldade;

    public TextoExercicio(String conteudo, String dificuldade) {
        this.conteudo    = conteudo;
        this.dificuldade = dificuldade;
    }

    public String getTexto() {
        return conteudo;
    }

    public String getConteudo()    {
        return conteudo;
    }
    public String getDificuldade() {
        return dificuldade;
    }
}