package main;

import feedback.FeedbackSonoro;
import feedback.FeedbackVisual;
import model.Jogador;
import model.Resultado;
import model.SessaoDigitacao;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Jogador jogador = new Jogador(1, "Arthur", 20, "Intermediário");

        SessaoDigitacao sessao = new SessaoDigitacao("Texto de teste");
        sessao.iniciarSessao();

        sessao.setTextoDigitado("Marca Joca Joca");
        Thread.sleep(1000);
        sessao.encerrarSessao();

        double wpmCalculado = sessao.calcularWPM();
        sessao.calcularTempoGasto();
        System.out.println();

        int caracteres = sessao.getQuantidadeCaracteres();

        Resultado resultado = new Resultado(wpmCalculado, 95, caracteres);
        resultado.exibirResultado();

        sessao.setResultado(resultado);

        sessao.adicionarFeedback(new FeedbackVisual());
        sessao.adicionarFeedback(new FeedbackSonoro());
        sessao.emitirTodosFeedbacks();
    }
}