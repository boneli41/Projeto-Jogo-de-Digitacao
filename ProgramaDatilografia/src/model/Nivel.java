package model;

public class Nivel {

    private String dificuldade;
    private int tempoLimite;
    private TextoExercicio textoExercicio;

    public Nivel(String dificuldade, int tempoLimite) {
        this.dificuldade = dificuldade;
        this.tempoLimite = tempoLimite;
    }

    public void definirParametros() {
        System.out.println("Nível: " + dificuldade + " | Tempo limite: " + tempoLimite + "s");
    }

    public void setTextoExercicio(TextoExercicio textoExercicio) {
        this.textoExercicio = textoExercicio;
    }

    public String getDificuldade()           {
        return dificuldade;
    }
    public int getTempoLimite()              {
        return tempoLimite;
    }
    public TextoExercicio getTextoExercicio(){
        return textoExercicio;
    }
}