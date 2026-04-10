package model;

import java.util.ArrayList;
import java.util.List;

public class Jogador extends Usuario {

    private String nivelHabilidade;
    private List<Jogo> jogos = new ArrayList<>();

    public Jogador(long id, String nome, int idade, String nivelHabilidade) {
        super(id, nome, idade);
        this.nivelHabilidade = nivelHabilidade;
    }

    public void iniciarTreino() {
        System.out.println(getNome() + " iniciou um treino. Nível: " + nivelHabilidade);
    }

    public void adicionarJogo(Jogo jogo) {
        jogos.add(jogo);
    }

    public String getNivelHabilidade() {
        return nivelHabilidade;
    }
    public List<Jogo> getJogos() {
        return jogos;
    }
}