package model;

import java.util.ArrayList;
import java.util.List;

public class Jogo {

    private String nome;
    private String versao;
    private List<Nivel> niveis = new ArrayList<>();
    private List<SessaoDigitacao> sessoes = new ArrayList<>();

    public Jogo(String nome, String versao) {
        this.nome    = nome;
        this.versao  = versao;
    }

    public void iniciarJogo() {
        System.out.println("model.Jogo '" + nome + "' v" + versao + " iniciado.");
    }

    public void encerrarJogo() {
        System.out.println("model.Jogo '" + nome + "' encerrado.");
    }

    public void adicionarNivel(Nivel nivel) {
        niveis.add(nivel);
    }

    public void adicionarSessao(SessaoDigitacao sessao) {
        sessoes.add(sessao);
    }

    public String getNome()                      {
        return nome;
    }
    public String getVersao()                    {
        return versao;
    }
    public List<Nivel> getNiveis()               {
        return niveis;
    }
    public List<SessaoDigitacao> getSessoes()    {
        return sessoes;
    }
}