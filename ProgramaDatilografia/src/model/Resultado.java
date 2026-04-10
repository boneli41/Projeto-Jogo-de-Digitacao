package model;

public class Resultado {

    private double wpm;
    private double precisao;
    private double caracteresDigitados;

    public Resultado(double wpm, double precisao, double caracteresDigitados) {
        this.wpm = wpm;
        this.precisao = precisao;
        this.caracteresDigitados = caracteresDigitados;
    }

    public void calcularPrecisao() {
        System.out.println("Precisão calculada: " + precisao + "%");
    }

    public void exibirResultado() {
        System.out.println("=== model.Resultado ===");
        System.out.println("WPM: " + wpm);
        System.out.println("Precisão: " + precisao + "%");
        System.out.println("Caracteres digitados: " + caracteresDigitados);
    }

    public double getWpm(){
        return wpm;
    }
    public double getPrecisao(){
        return precisao;
    }
    public double getCaracteresDigitados(){
        return caracteresDigitados;
    }
}