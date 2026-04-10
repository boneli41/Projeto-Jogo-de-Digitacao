package model;

import feedback.Feedback;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SessaoDigitacao {

    private LocalDateTime tempoInicio;
    private LocalDateTime tempoFim;
    private int qtdErros;
    private String textoOriginal;
    private String textoDigitado;
    private Resultado resultado;
    private List<Feedback> feedbacks = new ArrayList<>();

    public SessaoDigitacao(String textoOriginal) {
        this.textoOriginal = textoOriginal;
        this.qtdErros = 0;
    }

    public void iniciarSessao() {
        this.tempoInicio = LocalDateTime.now();
        System.out.println("Sessão iniciada em: " + tempoInicio);
    }

    public void encerrarSessao() {
        this.tempoFim = LocalDateTime.now();
        System.out.println("Sessão encerrada em: " + tempoFim);
    }

    public double calcularWPM() {
        if (textoDigitado == null || tempoInicio == null || tempoFim == null) {
            System.out.println("Dados insuficientes para calcular WPM.");
            return 0;
        }
        int caracteres = textoDigitado.length();
        long millis = ChronoUnit.MILLIS.between(tempoInicio, tempoFim);
        if (millis <= 0) {
            System.out.println("Tempo inválido.");
            return 0;
        }
        double minutos = millis / 60000.0;
        double wpm = (caracteres / 5.0) / minutos;
        System.out.println("WPM calculado: " + wpm);
        return wpm;
    }

    public void calcularTempoGasto() {
        if (tempoInicio != null && tempoFim != null) {
            long millies = ChronoUnit.MILLIS.between(tempoInicio, tempoFim);
            System.out.println("Tempo gasto: " + millies + " ms");
        }
    }
    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public void adicionarFeedback(Feedback feedback) {
        feedbacks.add(feedback);
    }
    public void emitirTodosFeedbacks() {
        for (Feedback f : feedbacks) {
            f.emitirFeedback();
        }
    }

    public int getQuantidadeCaracteres() {
        return textoDigitado != null ? textoDigitado.length() : 0;
    }

    public void setTextoDigitado(String textoDigitado) {
        this.textoDigitado = textoDigitado;
    }
    public void incrementarErros()                     {
        this.qtdErros++;
    }

    public LocalDateTime getTempoInicio()  {
        return tempoInicio;
    }
    public LocalDateTime getTempoFim()     {
        return tempoFim;
    }
    public int getQtdErros()           {
        return qtdErros;
    }
    public String getTextoOriginal()   {
        return textoOriginal;
    }
    public String getTextoDigitado()   {
        return textoDigitado;
    }
    public Resultado getResultado()    {
        return resultado;
    }
    public List<Feedback> getFeedbacks(){
        return feedbacks;
    }
}