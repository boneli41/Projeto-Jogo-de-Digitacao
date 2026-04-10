package model;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Usuario {

    private List<TextoExercicio> textos = new ArrayList<>();

    public Administrador(long id, String nome, int idade) {
        super(id, nome, idade);
    }

    public void cadastrarTexto(TextoExercicio texto) {
        textos.add(texto);
        System.out.println("Texto cadastrado: " + texto.getConteudo());
    }

    public void removerTexto(TextoExercicio texto) {
        textos.remove(texto);
        System.out.println("Texto removido.");
    }

    public List<TextoExercicio> getTextos() {
        return textos;
    }
}